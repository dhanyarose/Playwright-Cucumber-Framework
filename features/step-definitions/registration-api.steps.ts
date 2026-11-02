import { expect } from '@playwright/test';
import { Given, When, Then } from '@cucumber/cucumber';
import { request } from 'playwright';
import { CustomWorld } from '../support/world';
import { RegistrationApi } from '../support/registration-api';

Given('I have opened the registration API form', async function (this: CustomWorld) {
  const result = await this.registrationApi!.getRegistrationForm();
  expect(result.response.status()).toBe(200);
  this.registrationToken = result.token;
});

When('I submit valid registration data through the API', async function (this: CustomWorld) {
  const details = this.createRegistrationDetails();
  const result = await this.registrationApi!.submitRegistration(
    this.registrationToken!,
    details
  );
  this.apiResponseStatus = result.response.status();
  this.apiResponseBody = result.body;
});

When('I submit empty registration data through the API', async function (this: CustomWorld) {
  const result = await this.registrationApi!.submitRegistration(this.registrationToken!, {});
  this.apiResponseStatus = result.response.status();
  this.apiResponseBody = result.body;
});

When('I submit registration data with an invalid email through the API', async function (
  this: CustomWorld
) {
  const result = await this.registrationApi!.submitRegistration(
    this.registrationToken!,
    this.createRegistrationDetails({ email: 'invalid-email' })
  );
  this.apiResponseStatus = result.response.status();
  this.apiResponseBody = result.body;
});

When('I submit registration data with mismatched passwords through the API', async function (
  this: CustomWorld
) {
  const result = await this.registrationApi!.submitRegistration(
    this.registrationToken!,
    this.createRegistrationDetails({ confirmPassword: 'DifferentPass!' })
  );
  this.apiResponseStatus = result.response.status();
  this.apiResponseBody = result.body;
});

When('I submit the same valid registration data twice through the API', async function (
  this: CustomWorld
) {
  const details = this.createRegistrationDetails();
  const first = await this.registrationApi!.submitRegistration(this.registrationToken!, details);
  expect(first.response.status()).toBe(200);
  expect(first.body).toContain('Your registration completed');

  const anonymousContext = await request.newContext({ baseURL: this.baseUrl });
  this.additionalApiContexts.push(anonymousContext);
  const duplicateCheckApi = new RegistrationApi(anonymousContext, (metric) =>
    this.recordApiMetric(metric)
  );
  const form = await duplicateCheckApi.getRegistrationForm();
  const second = await duplicateCheckApi.submitRegistration(form.token, details);
  this.apiResponseStatus = second.response.status();
  this.apiResponseBody = second.body;
});

Then('the API registration succeeds', async function (this: CustomWorld) {
  expect(this.apiResponseStatus).toBe(200);
  expect(this.apiResponseBody).toContain('Your registration completed');
});

Then('the API reports all required registration fields', async function (
  this: CustomWorld
) {
  expect(this.apiResponseStatus).toBe(200);
  expect(this.apiResponseBody).toContain('First name is required.');
  expect(this.apiResponseBody).toContain('Last name is required.');
  expect(this.apiResponseBody).toContain('Email is required.');
  expect(this.apiResponseBody).toContain('Password is required.');
});

Then('the API reports an invalid email', async function (this: CustomWorld) {
  expect(this.apiResponseStatus).toBe(200);
  expect(this.apiResponseBody).toContain('Wrong email');
});

Then('the API reports a password mismatch', async function (this: CustomWorld) {
  expect(this.apiResponseStatus).toBe(200);
  expect(this.apiResponseBody).toContain(
    'The password and confirmation password do not match.'
  );
});

Then('the API reports that the email is already registered', async function (
  this: CustomWorld
) {
  expect(this.apiResponseStatus).toBe(200);
  expect(this.apiResponseBody).toContain('The specified email already exists');
});
