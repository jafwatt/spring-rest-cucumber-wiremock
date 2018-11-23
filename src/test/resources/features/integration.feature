Feature: My Feature

  Scenario: Create a new customer
    Given the application is running
    When I call the REST endpoint to create a new customer
    Then I get a successful response