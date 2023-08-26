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

public class MSCrearPropiedadSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public MSCrearPropiedadSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("a propiedad with valid details")
    public void getPropiedadValidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String titulo = data.get(0).get("titulo");
        String descripcion = data.get(0).get("descripcion");
        String precio = data.get(0).get("precio");
        int tipoPropiedad = Integer.parseInt(data.get(0).get("tipoPropiedad"));
        String personas = data.get(0).get("personas");
        String camas = data.get(0).get("camas");
        String habitaciones = data.get(0).get("habitaciones");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("titulo", titulo);
        valuesToTemplate.put("descripcion", descripcion);
        valuesToTemplate.put("precio", precio);
        valuesToTemplate.put("tipoPropiedad", tipoPropiedad);
        valuesToTemplate.put("personas", personas);
        valuesToTemplate.put("camas", camas);
        valuesToTemplate.put("habitaciones", habitaciones);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msalojamiento-crearpropiedad_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }
    @Given("a propiedad with invalid details")
    public void getPropiedadInvalidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String titulo = data.get(0).get("titulo");
        String descripcion = data.get(0).get("descripcion");
        String precio = data.get(0).get("precio");
        String tipoPropiedad = data.get(0).get("tipoPropiedad");
        String personas = data.get(0).get("personas");
        String camas = data.get(0).get("camas");
        String habitaciones = data.get(0).get("habitaciones");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("titulo", titulo);
        valuesToTemplate.put("descripcion", descripcion);
        valuesToTemplate.put("precio", precio);
        valuesToTemplate.put("tipoPropiedad", tipoPropiedad);
        valuesToTemplate.put("personas", personas);
        valuesToTemplate.put("camas", camas);
        valuesToTemplate.put("habitaciones", habitaciones);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msalojamiento-crearpropiedad_request"), valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for propiedad creation")
    public void submitPropiedadCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msalojamiento-service_url")
                        + envConfig.getProperty("msalojamiento-crearpropiedad_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the propiedad response is {int}")
    public void verifyHTTPResponseCodePropiedad(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a propiedad id is returned")
    public void checkPropiedadId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
    }
}
