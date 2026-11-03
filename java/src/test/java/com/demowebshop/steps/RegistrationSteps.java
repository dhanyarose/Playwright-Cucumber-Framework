package com.demowebshop.steps;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demowebshop.pages.RegistrationDetails;
import com.demowebshop.support.TestContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.regex.Pattern;

public class RegistrationSteps {
  private final TestContext context;

  public RegistrationSteps(TestContext context) {
    this.context = context;
  }

  @Given("I am on the login page")
  public void openLoginPage() {
    context.loginPage.open();
    assertThat(context.loginPage.heading()).isVisible();
  }

  @When("I click the Register link")
  public void clickRegisterLink() {
    context.loginPage.openRegistration();
  }

  @Given("I am on the registration page")
  public void openRegistrationPage() {
    context.registrationPage.open();
  }

  @When("I submit valid registration details")
  public void submitValidRegistration() {
    context.registrationPage.submitRegistration(RegistrationDetails.valid());
  }

  @When("I submit an empty registration form")
  public void submitEmptyRegistration() {
    context.registrationPage.submitRegistration(
        new RegistrationDetails(null, null, null, null, null, null));
  }

  @When("I submit registration with an invalid email")
  public void submitRegistrationWithInvalidEmail() {
    context.registrationPage.submitRegistration(
        RegistrationDetails.valid().withEmail("invalid-email"));
  }

  @When("I submit registration with mismatched passwords")
  public void submitRegistrationWithMismatchedPasswords() {
    context.registrationPage.submitRegistration(
        RegistrationDetails.valid().withConfirmation("DifferentPass!"));
  }

  @When("I submit registration with a password shorter than the minimum")
  public void submitRegistrationWithShortPassword() {
    context.registrationPage.submitRegistration(
        RegistrationDetails.valid().withPassword("abc", "abc"));
  }

  @Then("the registration page is displayed")
  public void registrationPageIsDisplayed() {
    assertThat(context.page).hasURL(Pattern.compile(".*/register$"));
    assertThat(context.registrationPage.heading()).isVisible();
  }

  @Then("registration completes successfully")
  public void registrationCompletesSuccessfully() {
    assertThat(context.page.getByText("Your registration completed",
        new Page.GetByTextOptions().setExact(true))).isVisible();
  }

  @Then("required-field validation errors are displayed")
  public void requiredFieldErrorsAreDisplayed() {
    assertThat(context.registrationPage.validationMessage("First name is required.")).isVisible();
    assertThat(context.registrationPage.validationMessage("Last name is required.")).isVisible();
    assertThat(context.registrationPage.validationMessage("Email is required.")).isVisible();
    assertEquals(2, context.registrationPage.validationMessage("Password is required.").count());
  }

  @Then("the invalid-email validation error is displayed")
  public void invalidEmailErrorIsDisplayed() {
    assertThat(context.registrationPage.validationMessage("Wrong email")).isVisible();
  }

  @Then("the password-mismatch validation error is displayed")
  public void passwordMismatchErrorIsDisplayed() {
    assertThat(context.registrationPage.validationMessage(
        "The password and confirmation password do not match.")).isVisible();
  }

  @Then("the password-length validation error is displayed")
  public void passwordLengthErrorIsDisplayed() {
    Locator errors = context.page.locator(".field-validation-error");
    assertThat(errors).containsText(
        Pattern.compile("password.*(minimum|at least|characters)", Pattern.CASE_INSENSITIVE));
  }
}
