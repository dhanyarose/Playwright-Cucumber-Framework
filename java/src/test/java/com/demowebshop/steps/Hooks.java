package com.demowebshop.steps;

import com.demowebshop.pages.PageObjectFactory;
import com.demowebshop.support.RegistrationApiClient;
import com.demowebshop.support.TestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
  private final TestContext context;

  public Hooks(TestContext context) {
    this.context = context;
  }

  @Before("@ui")
  public void startBrowser() {
    context.startPlaywright();
    String headed = System.getProperty("headed", System.getenv("HEADED"));
    context.browser = context.playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(!"true".equalsIgnoreCase(headed)));
    context.browserContext = context.browser.newContext(
        new Browser.NewContextOptions().setBaseURL(context.baseUrl()));
    context.page = context.browserContext.newPage();
    PageObjectFactory pages = new PageObjectFactory(context.page);
    context.loginPage = pages.loginPage();
    context.registrationPage = pages.registrationPage();
  }

  @Before("@api")
  public void startApiClient() {
    context.startPlaywright();
    context.registrationApi =
        new RegistrationApiClient(context.newApiRequestContext());
  }

  @After
  public void closeResources() {
    context.close();
  }
}
