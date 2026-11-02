@ui
Feature: Demo Web Shop registration
  As a visitor
  I want to register an account
  So that I can use the Demo Web Shop

  Scenario: Open registration from the login page
    Given I am on the login page
    When I click the Register link
    Then the registration page is displayed

  Scenario: Register with valid details
    Given I am on the registration page
    When I submit valid registration details
    Then registration completes successfully

  Scenario: Reject an empty registration form
    Given I am on the registration page
    When I submit an empty registration form
    Then required-field validation errors are displayed

  Scenario: Reject a malformed email
    Given I am on the registration page
    When I submit registration with an invalid email
    Then the invalid-email validation error is displayed

  Scenario: Reject mismatched passwords
    Given I am on the registration page
    When I submit registration with mismatched passwords
    Then the password-mismatch validation error is displayed

  Scenario: Reject a password shorter than the minimum
    Given I am on the registration page
    When I submit registration with a password shorter than the minimum
    Then the password-length validation error is displayed
