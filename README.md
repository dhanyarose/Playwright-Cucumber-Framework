<<<<<<< HEAD
# Demo Web Shop BDD tests

TypeScript page objects and Cucumber BDD scenarios for the Demo Web Shop
registration flow. UI scenarios use Playwright; API scenarios exercise the
site's actual `/register` HTML form endpoint, including its anti-forgery token.
This is a form-backed web endpoint, not a documented JSON API.

## Install and run

```powershell
npm install
npx playwright install chromium
npm test
```

Run only the browser or API acceptance scenarios:

```powershell
npm run test:ui
npm run test:api
```

Run the browser UI scenarios in a visible headed Chromium window:

```powershell
npm run test:ui:headed
```

The API scenarios use Playwright's HTTP request client, so they have no browser
window and do not support headed mode. Run them with `npm run test:api`.

Set `BASE_URL` to target a different compatible environment:

```powershell
$env:BASE_URL = "https://demowebshop.tricentis.com"
npm test
```

## Coverage

- UI: login-to-registration navigation, successful registration, required
  fields, malformed email, mismatched passwords, and minimum password length.
- API: successful registration, required fields, malformed email, mismatched
  passwords, and duplicate email.
- Successful scenarios create persistent accounts with unique synthetic
  `@example.com` email addresses.

## Reports and performance

Cucumber writes `reports/cucumber-report.json`. API scenarios also write
`reports/api-performance.json` with individual GET/POST response times plus
average, median, p95, and maximum. These are measurements from a small
acceptance-test run, not a load/throughput benchmark or a service-level
performance guarantee.
=======
# Playwright-Cucumber-Framework
>>>>>>> fd1e148b7bd7968683168ffba49d5799a3b852ce
