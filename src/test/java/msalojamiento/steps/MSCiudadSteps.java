package msalojamiento.steps;

import context.World;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import util.RequestSpecificationFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static util.Util.jsonTemplate;

public class MSCiudadSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public MSCiudadSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("a ciudad with valid details")
    public void getCiudadValidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String paisid = data.get(0).get("paisid");
        String nombre = data.get(0).get("nombre");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("nombre", nombre);
        valuesToTemplate.put("paisid", paisid);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msalojamiento-ciudad_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }
    @Given("a ciudad with invalid details")
    public void getCiudadInvalidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String nombre = data.get(0).get("nombre");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("paisid", "");
        valuesToTemplate.put("nombre", nombre);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msalojamiento-ciudad_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for ciudad creation")
    public void submitCiudadCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msalojamiento-service_url")
                        + envConfig.getProperty("msalojamiento-ciudad_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the Ciudad response is {int}")
    public void verifyHTTPResponseCodeCiudad(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a ciudad id is returned")
    public void checkCiudadId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
    }
}
