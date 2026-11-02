import type { Page, Locator } from 'playwright';

export class LoginPage {
  readonly heading: Locator;
  readonly registerLink: Locator;

  constructor(private readonly page: Page) {
    this.heading = page.getByRole('heading', { name: 'Welcome, Please Sign In!' });
    this.registerLink = page.getByRole('link', { name: 'Register' });
  }

  async open(): Promise<void> {
    await this.page.goto('/login');
  }

  async openRegistration(): Promise<void> {
    await this.registerLink.click();
  }
}
