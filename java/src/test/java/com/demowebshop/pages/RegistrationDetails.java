package com.demowebshop.pages;

import java.util.UUID;

public record RegistrationDetails(
    String firstName,
    String lastName,
    String email,
    String password,
    String confirmPassword,
    String gender) {

  public static RegistrationDetails valid() {
    return new RegistrationDetails(
        "Cucumber",
        "Tester",
        "cucumber-" + UUID.randomUUID() + "@example.com",
        "T3stPass!",
        "T3stPass!",
        "male");
  }

  public RegistrationDetails withEmail(String value) {
    return new RegistrationDetails(firstName, lastName, value, password, confirmPassword, gender);
  }

  public RegistrationDetails withPassword(String value, String confirmation) {
    return new RegistrationDetails(firstName, lastName, email, value, confirmation, gender);
  }

  public RegistrationDetails withConfirmation(String value) {
    return new RegistrationDetails(firstName, lastName, email, password, value, gender);
  }
}
