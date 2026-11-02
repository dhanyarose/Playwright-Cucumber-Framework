import {
  After,
  AfterAll,
  Before,
  Status,
  setDefaultTimeout
} from '@cucumber/cucumber';
import { request, chromium } from 'playwright';
import { CustomWorld, writeApiPerformanceReport } from './world';
import { RegistrationApi } from './registration-api';
import { LoginPage } from '../../pages/login-page';
import { RegistrationPage } from '../../pages/registration-page';

setDefaultTimeout(90000);

Before({ tags: '@ui' }, async function (this: CustomWorld) {
  this.browser = await chromium.launch({
    headless: process.env.HEADED !== 'true'
  });
  const context = await this.browser.newContext({ baseURL: this.baseUrl });
  this.page = await context.newPage();
  this.loginPage = new LoginPage(this.page);
  this.registrationPage = new RegistrationPage(this.page);
});

Before({ tags: '@api' }, async function (this: CustomWorld) {
  this.apiContext = await request.newContext({ baseURL: this.baseUrl });
  this.registrationApi = new RegistrationApi(this.apiContext, (metric) =>
    this.recordApiMetric(metric)
  );
});

After(async function (this: CustomWorld, scenario) {
  if (scenario.result?.status === Status.FAILED && this.page) {
    await this.attach(await this.page.screenshot({ fullPage: true }), 'image/png');
  }
  await this.apiContext?.dispose();
  await Promise.all(this.additionalApiContexts.map((context) => context.dispose()));
  await this.browser?.close();
});

AfterAll(writeApiPerformanceReport);
