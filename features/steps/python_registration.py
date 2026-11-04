import re
from uuid import uuid4

from behave import given, then, when
from playwright.sync_api import expect


def create_registration_details(**overrides):
    details = {
        "first_name": "Cucumber",
        "last_name": "Tester",
        "email": f"cucumber-{uuid4()}@example.com",
        "password": "T3stPass!",
        "confirm_password": "T3stPass!",
        "gender": "male",
    }
    details.update(overrides)
    return details


def submit_registration(page, details):
    if details.get("gender") == "male":
        page.get_by_label("Male", exact=True).check()
    elif details.get("gender") == "female":
        page.get_by_label("Female", exact=True).check()

    fields = {
        "first_name": "First name:",
        "last_name": "Last name:",
        "email": "Email:",
        "password": "Password:",
        "confirm_password": "Confirm password:",
    }
    for key, label in fields.items():
        if details.get(key) is not None:
            page.get_by_label(label, exact=True).fill(details[key])

    page.get_by_role("button", name="Register", exact=True).click()


def get_registration_form(api_context):
    response = api_context.get("/register")
    assert response.status == 200, (
        f"Expected GET /register to return 200, got {response.status}"
    )
    html = response.text()
    token_input = re.search(
        r'<input\b(?=[^>]*\bname=["\']__RequestVerificationToken["\'])[^>]*>',
        html,
        re.IGNORECASE | re.DOTALL,
    )
    if token_input is None:
        raise AssertionError(
            "Registration page did not include an anti-forgery token."
        )
    token_value = re.search(
        r'\bvalue=["\']([^"\']+)["\']',
        token_input.group(0),
        re.IGNORECASE,
    )
    if token_value is None:
        raise AssertionError(
            "Registration anti-forgery token did not include a value."
        )
    return token_value.group(1)


def submit_registration_api(api_context, token, details):
    return api_context.post(
        "/register",
        form={
            "__RequestVerificationToken": token,
            "Gender": "F" if details.get("gender") == "female" else "M",
            "FirstName": details.get("first_name") or "",
            "LastName": details.get("last_name") or "",
            "Email": details.get("email") or "",
            "Password": details.get("password") or "",
            "ConfirmPassword": details.get("confirm_password") or "",
            "register-button": "Register",
        },
    )


def record_api_response(context, response):
    context.api_response_status = response.status
    context.api_response_body = response.text()


def submit_api_details(context, details):
    response = submit_registration_api(
        context.api_context, context.registration_token, details
    )
    record_api_response(context, response)


def validation_message(page, message):
    return page.locator(".field-validation-error").filter(has_text=message)


@given("I am on the login page")
def step_on_login_page(context):
    context.page.goto("/login")
    expect(
        context.page.get_by_role(
            "heading", name="Welcome, Please Sign In!"
        )
    ).to_be_visible()


@when("I click the Register link")
def step_click_register_link(context):
    context.page.get_by_role("link", name="Register").click()


@given("I am on the registration page")
def step_on_registration_page(context):
    context.page.goto("/register")


@when("I submit valid registration details")
def step_submit_valid_registration(context):
    submit_registration(context.page, create_registration_details())


@when("I submit valid registration details with the female gender option")
def step_submit_female_registration(context):
    submit_registration(
        context.page, create_registration_details(gender="female")
    )


@when("I submit valid registration details with a six-character password")
def step_submit_minimum_password_registration(context):
    submit_registration(
        context.page,
        create_registration_details(password="T3st6!", confirm_password="T3st6!"),
    )


@when("I submit an empty registration form")
def step_submit_empty_registration(context):
    submit_registration(context.page, {})


@when("I submit registration with an invalid email")
def step_submit_invalid_email_registration(context):
    submit_registration(
        context.page, create_registration_details(email="invalid-email")
    )


@when("I submit registration with mismatched passwords")
def step_submit_mismatched_password_registration(context):
    submit_registration(
        context.page,
        create_registration_details(confirm_password="DifferentPass!"),
    )


@when("I submit registration with a password shorter than the minimum")
def step_submit_short_password_registration(context):
    submit_registration(
        context.page,
        create_registration_details(password="abc", confirm_password="abc"),
    )


@then("the registration page is displayed")
def step_registration_page_is_displayed(context):
    expect(context.page).to_have_url(re.compile(r"/register$"))
    expect(
        context.page.get_by_role("heading", name="Register", exact=True)
    ).to_be_visible()


@then("registration completes successfully")
def step_registration_completes(context):
    expect(
        context.page.get_by_text("Your registration completed", exact=True)
    ).to_be_visible()


@then("required-field validation errors are displayed")
def step_required_field_errors_displayed(context):
    expect(validation_message(context.page, "First name is required.")).to_be_visible()
    expect(validation_message(context.page, "Last name is required.")).to_be_visible()
    expect(validation_message(context.page, "Email is required.")).to_be_visible()
    expect(validation_message(context.page, "Password is required.")).to_have_count(2)


@then("the invalid-email validation error is displayed")
def step_invalid_email_error_displayed(context):
    expect(validation_message(context.page, "Wrong email")).to_be_visible()


@then("the password-mismatch validation error is displayed")
def step_password_mismatch_error_displayed(context):
    expect(
        validation_message(
            context.page,
            "The password and confirmation password do not match.",
        )
    ).to_be_visible()


@then("the password-length validation error is displayed")
def step_password_length_error_displayed(context):
    expect(
        validation_message(
            context.page,
            "The password should have at least 6 characters.",
        )
    ).to_be_visible()


@given("I have opened the registration API form")
def step_open_registration_api_form(context):
    context.registration_token = get_registration_form(context.api_context)


@when("I submit valid registration data through the API")
def step_submit_valid_registration_api(context):
    submit_api_details(context, create_registration_details())


@when(
    "I submit valid registration data with the female gender option through the API"
)
def step_submit_female_registration_api(context):
    submit_api_details(context, create_registration_details(gender="female"))


@when(
    "I submit valid registration data with a six-character password through the API"
)
def step_submit_minimum_password_registration_api(context):
    submit_api_details(
        context,
        create_registration_details(
            password="T3st6!", confirm_password="T3st6!"
        ),
    )


@when("I submit empty registration data through the API")
def step_submit_empty_registration_api(context):
    submit_api_details(context, {})


@when("I submit registration data with an invalid email through the API")
def step_submit_invalid_email_registration_api(context):
    submit_api_details(
        context, create_registration_details(email="invalid-email")
    )


@when("I submit registration data with mismatched passwords through the API")
def step_submit_mismatched_password_registration_api(context):
    submit_api_details(
        context,
        create_registration_details(confirm_password="DifferentPass!"),
    )


@when("I submit the same valid registration data twice through the API")
def step_submit_duplicate_registration_api(context):
    details = create_registration_details()
    first_response = submit_registration_api(
        context.api_context, context.registration_token, details
    )
    first_body = first_response.text()
    assert first_response.status == 200, (
        f"Expected first registration to return 200, got {first_response.status}"
    )
    assert "Your registration completed" in first_body, (
        "Expected the first registration to succeed before checking for a duplicate."
    )

    duplicate_context = context.playwright.request.new_context(
        base_url=context.base_url
    )
    context.additional_api_contexts.append(duplicate_context)
    duplicate_token = get_registration_form(duplicate_context)
    duplicate_response = submit_registration_api(
        duplicate_context, duplicate_token, details
    )
    record_api_response(context, duplicate_response)


@then("the API registration succeeds")
def step_api_registration_succeeds(context):
    assert context.api_response_status == 200
    assert "Your registration completed" in context.api_response_body


@then("the API reports all required registration fields")
def step_api_required_fields_reported(context):
    assert context.api_response_status == 200
    for message in (
        "First name is required.",
        "Last name is required.",
        "Email is required.",
        "Password is required.",
    ):
        assert message in context.api_response_body, (
            f"Expected API validation response to contain: {message}"
        )


@then("the API reports an invalid email")
def step_api_invalid_email_reported(context):
    assert context.api_response_status == 200
    assert "Wrong email" in context.api_response_body


@then("the API reports a password mismatch")
def step_api_password_mismatch_reported(context):
    assert context.api_response_status == 200
    assert (
        "The password and confirmation password do not match."
        in context.api_response_body
    )


@then("the API reports that the email is already registered")
def step_api_duplicate_email_reported(context):
    assert context.api_response_status == 200
    assert "The specified email already exists" in context.api_response_body
