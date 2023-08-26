@Propiedad
Feature: Perform a Propiedad creation

  Scenario: Perform propiedad creation with valid details
    Given a propiedad with valid details
      | titulo        | Propiedad Porongo |
      | descripcion   | Descripcion de propiedad porongo |
      | precio        | 20.0 |
      | tipoPropiedad | 1 |
      | personas      | 10 |
      | camas         | 10 |
      | habitaciones  | 5 |
    When request is submitted for propiedad creation
    Then verify that the propiedad response is 200
    And a propiedad id is returned

  Scenario: Perform a failed propiedad creation
    Given a propiedad with invalid details
      | titulo        | Propiedad Porongo |
      | descripcion   | Descripcion de propiedad porongo |
      | precio        | 20.0 |
      | tipoPropiedad | 1 |
      | personas      | -1 |
      | camas         | -1 |
      | habitaciones  | 5 |
    When request is submitted for propiedad creation
    Then verify that the propiedad response is 400