package com.demowebshop.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;

public class RegistrationPage {
  private final Page page;
  private final Locator heading;
  private final Locator maleRadio;
  private final Locator femaleRadio;
  private final Locator firstName;
  private final Locator lastName;
  private final Locator email;
  private final Locator password;
  private final Locator confirmPassword;
  private final Locator registerButton;

  public RegistrationPage(Page page) {
    this.page = page;
    this.heading = page.getByRole(AriaRole.HEADING,
        new Page.GetByRoleOptions().setName("Register").setExact(true));
    this.maleRadio = page.locator("#gender-male");
    this.femaleRadio = page.locator("#gender-female");
    this.firstName = page.locator("#FirstName");
    this.lastName = page.locator("#LastName");
    this.email = page.locator("#Email");
    this.password = page.locator("#Password");
    this.confirmPassword = page.locator("#ConfirmPassword");
    this.registerButton = page.locator("#register-button");
  }

  public void open() {
    page.navigate("/register", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
  }

  public void submitRegistration(RegistrationDetails details) {
    if (details.gender() != null) {
      (details.gender().equals("female") ? femaleRadio : maleRadio).check();
    }
    if (details.firstName() != null) firstName.fill(details.firstName());
    if (details.lastName() != null) lastName.fill(details.lastName());
    if (details.email() != null) email.fill(details.email());
    if (details.password() != null) password.fill(details.password());
    if (details.confirmPassword() != null) confirmPassword.fill(details.confirmPassword());
    registerButton.click();
  }

  public Locator heading() {
    return heading;
  }

  public Locator validationMessage(String message) {
    return page.locator(".field-validation-error")
        .filter(new Locator.FilterOptions().setHasText(message));
  }
}
