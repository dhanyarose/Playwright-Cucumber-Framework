package com.demowebshop.pages;

import com.microsoft.playwright.Page;

public class PageObjectFactory {
  private final Page page;

  public PageObjectFactory(Page page) {
    this.page = page;
  }

  public LoginPage loginPage() {
    return new LoginPage(page);
  }

  public RegistrationPage registrationPage() {
    return new RegistrationPage(page);
  }
}
