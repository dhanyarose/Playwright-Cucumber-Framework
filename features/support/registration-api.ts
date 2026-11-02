import type { APIRequestContext, APIResponse } from 'playwright';
import type { ApiMetric } from './world';
import type { RegistrationDetails } from '../../pages/registration-page';

export class RegistrationApi {
  constructor(
    private readonly context: APIRequestContext,
    private readonly recordMetric: (metric: ApiMetric) => void
  ) {}

  async getRegistrationForm(): Promise<{ response: APIResponse; html: string; token: string }> {
    const started = performance.now();
    const response = await this.context.get('/register');
    const html = await response.text();
    this.record(response, '/register', 'GET', started);

    const input = html.match(
      /<input\b[^>]*name=["']__RequestVerificationToken["'][^>]*>/i
    )?.[0];
    const token = input?.match(/value=["']([^"']+)["']/i)?.[1];
    if (!token) {
      throw new Error('Registration page did not include an anti-forgery token.');
    }
    return { response, html, token };
  }

  async submitRegistration(
    token: string,
    details: RegistrationDetails
  ): Promise<{ response: APIResponse; body: string }> {
    const started = performance.now();
    const response = await this.context.post('/register', {
      form: {
        __RequestVerificationToken: token,
        Gender: details.gender === 'female' ? 'F' : 'M',
        FirstName: details.firstName ?? '',
        LastName: details.lastName ?? '',
        Email: details.email ?? '',
        Password: details.password ?? '',
        ConfirmPassword: details.confirmPassword ?? '',
        'register-button': 'Register'
      }
    });
    const body = await response.text();
    this.record(response, '/register', 'POST', started);
    return { response, body };
  }

  private record(
    response: APIResponse,
    path: string,
    method: string,
    started: number
  ): void {
    this.recordMetric({
      method,
      path,
      status: response.status(),
      durationMs: Number((performance.now() - started).toFixed(2))
    });
  }
}
