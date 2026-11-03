package com.demowebshop.support;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationApiClient {
  private static final Pattern TOKEN_INPUT =
      Pattern.compile("(?is)<input\\b(?=[^>]*\\bname=[\\\"']__RequestVerificationToken[\\\"'])[^>]*>");
  private static final Pattern VALUE_ATTRIBUTE =
      Pattern.compile("\\bvalue=[\\\"']([^\\\"']+)[\\\"']", Pattern.CASE_INSENSITIVE);
  private final APIRequestContext request;

  public RegistrationApiClient(APIRequestContext request) {
    this.request = request;
  }

  public RegistrationForm getRegistrationForm() {
    APIResponse response = request.get("/register");
    try {
      String html = response.text();
      Matcher input = TOKEN_INPUT.matcher(html);
      if (!input.find()) {
        throw new IllegalStateException("Registration page did not include an anti-forgery token.");
      }
      Matcher value = VALUE_ATTRIBUTE.matcher(input.group());
      if (!value.find()) {
        throw new IllegalStateException("Registration anti-forgery token had no value.");
      }
      return new RegistrationForm(response.status(), value.group(1));
    } finally {
      response.dispose();
    }
  }

  public RegistrationResult submitRegistration(String token, RegistrationDetails details) {
    FormData form = FormData.create()
        .set("__RequestVerificationToken", token)
        .set("Gender", details.gender() != null && details.gender().equals("female") ? "F" : "M")
        .set("FirstName", valueOrEmpty(details.firstName()))
        .set("LastName", valueOrEmpty(details.lastName()))
        .set("Email", valueOrEmpty(details.email()))
        .set("Password", valueOrEmpty(details.password()))
        .set("ConfirmPassword", valueOrEmpty(details.confirmPassword()))
        .set("register-button", "Register");

    APIResponse response = request.post("/register", RequestOptions.create().setForm(form));
    try {
      return new RegistrationResult(response.status(), response.text());
    } finally {
      response.dispose();
    }
  }

  private String valueOrEmpty(String value) {
    return value == null ? "" : value;
  }

  public record RegistrationForm(int status, String token) {}

  public record RegistrationResult(int status, String body) {}

  public record RegistrationDetails(
      String firstName,
      String lastName,
      String email,
      String password,
      String confirmPassword,
      String gender) {}
}
