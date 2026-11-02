import { randomUUID } from 'node:crypto';
import { mkdirSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import {
  setWorldConstructor,
  World,
  type IWorldOptions
} from '@cucumber/cucumber';
import type { APIRequestContext, Browser, Page } from 'playwright';
import { LoginPage } from '../../pages/login-page';
import { RegistrationPage, type RegistrationDetails } from '../../pages/registration-page';
import type { RegistrationApi } from './registration-api';

export interface ApiMetric {
  method: string;
  path: string;
  status: number;
  durationMs: number;
}

export class CustomWorld extends World {
  readonly baseUrl: string;
  browser?: Browser;
  page?: Page;
  loginPage?: LoginPage;
  registrationPage?: RegistrationPage;
  apiContext?: APIRequestContext;
  additionalApiContexts: APIRequestContext[] = [];
  registrationApi?: RegistrationApi;
  registrationDetails?: RegistrationDetails;
  registrationToken?: string;
  apiResponseBody?: string;
  apiResponseStatus?: number;
  readonly apiMetrics: ApiMetric[] = [];

  constructor(options: IWorldOptions) {
    super(options);
    this.baseUrl = String(options.parameters.baseUrl);
  }

  createRegistrationDetails(
    overrides: RegistrationDetails = {}
  ): RegistrationDetails {
    this.registrationDetails = {
      firstName: 'Cucumber',
      lastName: 'Tester',
      email: `cucumber-${randomUUID()}@example.com`,
      password: 'T3stPass!',
      confirmPassword: 'T3stPass!',
      gender: 'male',
      ...overrides
    };
    return this.registrationDetails;
  }

  recordApiMetric(metric: ApiMetric): void {
    this.apiMetrics.push(metric);
    allApiMetrics.push(metric);
  }
}

setWorldConstructor(CustomWorld);

export function writeApiPerformanceReport(): void {
  const metrics = allApiMetrics;
  if (metrics.length === 0) return;

  const durations = metrics.map((metric) => metric.durationMs).sort((a, b) => a - b);
  const percentile = (fraction: number): number =>
    durations[Math.max(0, Math.ceil(durations.length * fraction) - 1)];
  const report = {
    generatedAt: new Date().toISOString(),
    baseUrl: process.env.BASE_URL || 'https://demowebshop.tricentis.com',
    note: 'Acceptance-test response times; this is not a load or throughput test.',
    requestCount: metrics.length,
    summary: {
      averageMs: Number(
        (durations.reduce((total, duration) => total + duration, 0) / durations.length).toFixed(2)
      ),
      medianMs: percentile(0.5),
      p95Ms: percentile(0.95),
      maxMs: durations[durations.length - 1]
    },
    requests: metrics
  };

  mkdirSync(resolve('reports'), { recursive: true });
  writeFileSync(resolve('reports', 'api-performance.json'), `${JSON.stringify(report, null, 2)}\n`);
  const markdown = [
    '# API performance report',
    '',
    `- Generated: ${report.generatedAt}`,
    `- Target: ${report.baseUrl}`,
    `- Requests measured: ${report.requestCount}`,
    `- Scope: ${report.note}`,
    '',
    '## Response-time summary',
    '',
    '| Metric | Time (ms) |',
    '| --- | ---: |',
    `| Average | ${report.summary.averageMs} |`,
    `| Median | ${report.summary.medianMs} |`,
    `| P95 | ${report.summary.p95Ms} |`,
    `| Maximum | ${report.summary.maxMs} |`,
    '',
    '## Request timings',
    '',
    '| Method | Path | Status | Duration (ms) |',
    '| --- | --- | ---: | ---: |',
    ...metrics.map(
      (metric) =>
        `| ${metric.method} | ${metric.path} | ${metric.status} | ${metric.durationMs} |`
    ),
    ''
  ].join('\n');
  writeFileSync(resolve('reports', 'api-performance.md'), markdown);
  console.log(
    `API performance: ${report.requestCount} requests; average ${report.summary.averageMs} ms, ` +
      `median ${report.summary.medianMs} ms, p95 ${report.summary.p95Ms} ms, ` +
      `max ${report.summary.maxMs} ms.`
  );
}

export const allApiMetrics: ApiMetric[] = [];
