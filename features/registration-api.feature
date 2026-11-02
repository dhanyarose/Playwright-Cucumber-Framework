@api
Feature: Demo Web Shop registration form API
  As a client of the registration form endpoint
  I want valid requests to register and invalid requests to be rejected
  So that registration rules are enforced server-side

  Scenario: Register through the form endpoint with valid data
    Given I have opened the registration API form
    When I submit valid registration data through the API
    Then the API registration succeeds

  Scenario: Reject empty registration data through the API
    Given I have opened the registration API form
    When I submit empty registration data through the API
    Then the API reports all required registration fields

  Scenario: Reject a malformed email through the API
    Given I have opened the registration API form
    When I submit registration data with an invalid email through the API
    Then the API reports an invalid email

  Scenario: Reject mismatched passwords through the API
    Given I have opened the registration API form
    When I submit registration data with mismatched passwords through the API
    Then the API reports a password mismatch

  Scenario: Reject a duplicate email through the API
    Given I have opened the registration API form
    When I submit the same valid registration data twice through the API
    Then the API reports that the email is already registered
