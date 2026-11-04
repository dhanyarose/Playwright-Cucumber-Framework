# Python Behave + Playwright

The Python suite reuses the scenarios in `../features/` and implements both
the registration UI and form-endpoint cases without changing the TypeScript or
Java implementations.

## Setup

From the repository root, install the Python dependencies and Chromium:

```powershell
python -m pip install -r python/requirements.txt
python -m playwright install chromium
```

Set `BASE_URL` to a compatible test environment when needed; it defaults to
`https://demowebshop.tricentis.com`. Successful registration scenarios create
accounts, so use a test environment when available.

## Run

Run the UI scenarios with a visible browser:

```powershell
$env:HEADED = "true"
python -m behave features --tags=@ui
```

Run the form-endpoint scenarios (no browser window is needed):

```powershell
python -m behave features --tags=@api
```

Run both sets:

```powershell
$env:HEADED = "true"
python -m behave features
```
