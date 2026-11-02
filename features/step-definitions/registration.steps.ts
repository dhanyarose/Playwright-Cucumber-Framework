import { expect } from '@playwright/test';
import { Given, When, Then } from '@cucumber/cucumber';
import { CustomWorld } from '../support/world';

Given('I am on the login page', async function (this: CustomWorld) {
  await this.loginPage!.open();
  await expect(this.loginPage!.heading).toBeVisible();
});

When('I click the Register link', async function (this: CustomWorld) {
  await this.loginPage!.openRegistration();
});

Given('I am on the registration page', async function (this: CustomWorld) {
  await this.registrationPage!.open();
});

When('I submit valid registration details', async function (this: CustomWorld) {
  await this.registrationPage!.submitRegistration(this.createRegistrationDetails());
});

When('I submit an empty registration form', async function (this: CustomWorld) {
  await this.registrationPage!.submitRegistration({});
});

When('I submit registration with an invalid email', async function (this: CustomWorld) {
  await this.registrationPage!.submitRegistration(
    this.createRegistrationDetails({ email: 'invalid-email' })
  );
});

When('I submit registration with mismatched passwords', async function (this: CustomWorld) {
  await this.registrationPage!.submitRegistration(
    this.createRegistrationDetails({ confirmPassword: 'DifferentPass!' })
  );
});

When('I submit registration with a password shorter than the minimum', async function (
  this: CustomWorld
) {
  await this.registrationPage!.submitRegistration(
    this.createRegistrationDetails({ password: 'abc', confirmPassword: 'abc' })
  );
});

Then('the registration page is displayed', async function (this: CustomWorld) {
  await expect(this.page!).toHaveURL(/\/register$/);
  await expect(this.registrationPage!.heading).toBeVisible();
});

Then('registration completes successfully', async function (this: CustomWorld) {
  await expect(this.page!.getByText('Your registration completed', { exact: true })).toBeVisible();
});

Then('required-field validation errors are displayed', async function (this: CustomWorld) {
  await expect(
    this.registrationPage!.validationMessage('First name is required.')
  ).toBeVisible();
  await expect(
    this.registrationPage!.validationMessage('Last name is required.')
  ).toBeVisible();
  await expect(this.registrationPage!.validationMessage('Email is required.')).toBeVisible();
  await expect(
    this.registrationPage!.validationMessage('Password is required.')
  ).toHaveCount(2);
});

Then('the invalid-email validation error is displayed', async function (this: CustomWorld) {
  await expect(this.registrationPage!.validationMessage('Wrong email')).toBeVisible();
});

Then('the password-mismatch validation error is displayed', async function (
  this: CustomWorld
) {
  await expect(
    this.registrationPage!.validationMessage(
      'The password and confirmation password do not match.'
    )
  ).toBeVisible();
});

Then('the password-length validation error is displayed', async function (
  this: CustomWorld
) {
  await expect(this.page!.locator('.field-validation-error')).toContainText(
    /password.*(minimum|at least|characters)/i
  );
});
