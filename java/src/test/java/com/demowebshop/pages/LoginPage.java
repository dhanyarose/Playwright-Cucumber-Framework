package com.demowebshop.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;

public class LoginPage {
  private final Page page;
  private final Locator heading;
  private final Locator registerLink;

  public LoginPage(Page page) {
    this.page = page;
    this.heading = page.getByRole(AriaRole.HEADING,
        new Page.GetByRoleOptions().setName("Welcome, Please Sign In!"));
    this.registerLink = page.getByRole(AriaRole.LINK,
        new Page.GetByRoleOptions().setName("Register"));
  }

  public void open() {
    page.navigate("/login", new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
  }

  public void openRegistration() {
    registerLink.click();
  }

  public Locator heading() {
    return heading;
  }
}
