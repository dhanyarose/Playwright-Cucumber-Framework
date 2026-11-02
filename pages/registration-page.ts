import type { Locator, Page } from 'playwright';

export interface RegistrationDetails {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
  gender?: 'male' | 'female';
}

export class RegistrationPage {
  readonly heading: Locator;
  readonly maleRadio: Locator;
  readonly femaleRadio: Locator;
  readonly firstName: Locator;
  readonly lastName: Locator;
  readonly email: Locator;
  readonly password: Locator;
  readonly confirmPassword: Locator;
  readonly registerButton: Locator;

  constructor(private readonly page: Page) {
    this.heading = page.getByRole('heading', { name: 'Register', exact: true });
    this.maleRadio = page.locator('#gender-male');
    this.femaleRadio = page.locator('#gender-female');
    this.firstName = page.locator('#FirstName');
    this.lastName = page.locator('#LastName');
    this.email = page.locator('#Email');
    this.password = page.locator('#Password');
    this.confirmPassword = page.locator('#ConfirmPassword');
    this.registerButton = page.locator('#register-button');
  }

  async open(): Promise<void> {
    await this.page.goto('/register');
  }

  async submitRegistration(details: RegistrationDetails): Promise<void> {
    if (details.gender === 'male') await this.maleRadio.check();
    if (details.gender === 'female') await this.femaleRadio.check();
    if (details.firstName !== undefined) await this.firstName.fill(details.firstName);
    if (details.lastName !== undefined) await this.lastName.fill(details.lastName);
    if (details.email !== undefined) await this.email.fill(details.email);
    if (details.password !== undefined) await this.password.fill(details.password);
    if (details.confirmPassword !== undefined) {
      await this.confirmPassword.fill(details.confirmPassword);
    }
    await this.registerButton.click();
  }

  validationMessage(message: string): Locator {
    return this.page.locator('.field-validation-error').filter({ hasText: message });
  }
}
