@Pais
Feature: Perform a Pais creation

  Scenario: Perform pais creation with valid details
    Given a pais with valid details
      | nombre      | Bolivia2 |
      | codigopais  | BO2       |
    When request is submitted for pais creation
    Then verify that the Pais response is 200
    And a pais id is returned

  Scenario: Perform a failed pais creation
    Given a pais with invalid details
      | codigopais      | BO2       |
    When request is submitted for pais creation
    Then verify that the Pais response is 400