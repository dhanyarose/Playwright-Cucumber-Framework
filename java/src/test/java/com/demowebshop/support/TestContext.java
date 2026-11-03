package com.demowebshop.support;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.demowebshop.pages.LoginPage;
import com.demowebshop.pages.RegistrationPage;
import java.util.ArrayList;
import java.util.List;

public class TestContext {
  private final List<APIRequestContext> apiContexts = new ArrayList<>();
  public Playwright playwright;
  public Browser browser;
  public BrowserContext browserContext;
  public Page page;
  public LoginPage loginPage;
  public RegistrationPage registrationPage;
  public RegistrationApiClient registrationApi;
  public String registrationToken;
  public int apiResponseStatus;
  public String apiResponseBody;

  public String baseUrl() {
    String configured = System.getProperty("baseUrl");
    if (configured == null || configured.isBlank()) configured = System.getenv("BASE_URL");
    return configured == null || configured.isBlank()
        ? "https://demowebshop.tricentis.com"
        : configured;
  }

  public void startPlaywright() {
    playwright = Playwright.create();
  }

  public APIRequestContext newApiRequestContext() {
    APIRequestContext request = playwright.request().newContext(
        new APIRequest.NewContextOptions().setBaseURL(baseUrl()));
    apiContexts.add(request);
    return request;
  }

  public void close() {
    try {
      for (APIRequestContext apiContext : apiContexts) apiContext.dispose();
    } finally {
      try {
        if (browserContext != null) browserContext.close();
      } finally {
        try {
          if (browser != null) browser.close();
        } finally {
          if (playwright != null) playwright.close();
        }
      }
    }
  }
}
