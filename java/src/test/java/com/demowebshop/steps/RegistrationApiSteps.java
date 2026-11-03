package com.demowebshop.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demowebshop.support.RegistrationApiClient;
import com.demowebshop.support.RegistrationApiClient.RegistrationForm;
import com.demowebshop.support.RegistrationApiClient.RegistrationResult;
import com.demowebshop.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RegistrationApiSteps {
  private final TestContext context;

  public RegistrationApiSteps(TestContext context) {
    this.context = context;
  }

  @Given("I have opened the registration API form")
  public void openRegistrationApiForm() {
    RegistrationForm form = context.registrationApi.getRegistrationForm();
    assertEquals(200, form.status());
    context.registrationToken = form.token();
  }

  @When("I submit valid registration data through the API")
  public void submitValidRegistrationData() {
    saveResult(context.registrationApi.submitRegistration(
        context.registrationToken, validDetails()));
  }

  @When("I submit empty registration data through the API")
  public void submitEmptyRegistrationData() {
    RegistrationApiClient.RegistrationDetails details =
        new RegistrationApiClient.RegistrationDetails(null, null, null, null, null, null);
    saveResult(context.registrationApi.submitRegistration(context.registrationToken, details));
  }

  @When("I submit registration data with an invalid email through the API")
  public void submitInvalidEmailThroughApi() {
    saveResult(context.registrationApi.submitRegistration(
        context.registrationToken, validDetails("invalid-email")));
  }

  @When("I submit registration data with mismatched passwords through the API")
  public void submitMismatchedPasswordsThroughApi() {
    RegistrationApiClient.RegistrationDetails details = validDetails();
    details = new RegistrationApiClient.RegistrationDetails(
        details.firstName(), details.lastName(), details.email(), details.password(),
        "DifferentPass!", details.gender());
    saveResult(context.registrationApi.submitRegistration(context.registrationToken, details));
  }

  @When("I submit the same valid registration data twice through the API")
  public void submitDuplicateRegistrationThroughApi() {
    RegistrationApiClient.RegistrationDetails details = validDetails();
    RegistrationResult first =
        context.registrationApi.submitRegistration(context.registrationToken, details);
    assertEquals(200, first.status());
    assertTrue(first.body().contains("Your registration completed"));

    RegistrationApiClient duplicateCheckApi =
        new RegistrationApiClient(context.newApiRequestContext());
    RegistrationForm freshForm = duplicateCheckApi.getRegistrationForm();
    assertEquals(200, freshForm.status());
    saveResult(duplicateCheckApi.submitRegistration(freshForm.token(), details));
  }

  @Then("the API registration succeeds")
  public void apiRegistrationSucceeds() {
    assertEquals(200, context.apiResponseStatus);
    assertTrue(context.apiResponseBody.contains("Your registration completed"));
  }

  @Then("the API reports all required registration fields")
  public void apiReportsRequiredFields() {
    assertEquals(200, context.apiResponseStatus);
    assertTrue(context.apiResponseBody.contains("First name is required."));
    assertTrue(context.apiResponseBody.contains("Last name is required."));
    assertTrue(context.apiResponseBody.contains("Email is required."));
    assertTrue(context.apiResponseBody.contains("Password is required."));
  }

  @Then("the API reports an invalid email")
  public void apiReportsInvalidEmail() {
    assertEquals(200, context.apiResponseStatus);
    assertTrue(context.apiResponseBody.contains("Wrong email"));
  }

  @Then("the API reports a password mismatch")
  public void apiReportsPasswordMismatch() {
    assertEquals(200, context.apiResponseStatus);
    assertTrue(context.apiResponseBody.contains(
        "The password and confirmation password do not match."));
  }

  @Then("the API reports that the email is already registered")
  public void apiReportsDuplicateEmail() {
    assertEquals(200, context.apiResponseStatus);
    assertTrue(context.apiResponseBody.contains("The specified email already exists"));
  }

  private RegistrationApiClient.RegistrationDetails validDetails() {
    return validDetails("cucumber-" + java.util.UUID.randomUUID() + "@example.com");
  }

  private RegistrationApiClient.RegistrationDetails validDetails(String email) {
    return new RegistrationApiClient.RegistrationDetails(
        "Cucumber", "Tester", email, "T3stPass!", "T3stPass!", "male");
  }

  private void saveResult(RegistrationResult result) {
    context.apiResponseStatus = result.status();
    context.apiResponseBody = result.body();
  }
}
