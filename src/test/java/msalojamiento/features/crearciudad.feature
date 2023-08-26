@Ciudad
Feature: Perform a Ciudad creation

  Scenario: Perform ciudad creation with valid details
    Given a ciudad with valid details
      | nombre      | Porongo |
      | paisid      | 6f48094b-66fe-49d2-99f6-9a2a7db87406 |
    When request is submitted for ciudad creation
    Then verify that the Ciudad response is 200
    And a ciudad id is returned

  Scenario: Perform a failed ciudad creation
    Given a ciudad with invalid details
      | nombre      | Porongo |
    When request is submitted for ciudad creation
    Then verify that the Ciudad response is 400