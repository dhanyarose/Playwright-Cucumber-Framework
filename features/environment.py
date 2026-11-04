import os

from playwright.sync_api import sync_playwright


DEFAULT_BASE_URL = "https://demowebshop.tricentis.com"


def before_scenario(context, scenario):
    context.base_url = os.environ.get("BASE_URL", DEFAULT_BASE_URL).rstrip("/")
    tags = scenario.effective_tags
    context.playwright = sync_playwright().start()
    context.browser = None
    context.browser_context = None
    context.page = None
    context.api_context = None
    context.additional_api_contexts = []

    if "ui" in tags:
        headed = os.environ.get("HEADED", "").strip().lower() in {
            "1",
            "true",
            "yes",
        }
        context.browser = context.playwright.chromium.launch(headless=not headed)
        context.browser_context = context.browser.new_context(
            base_url=context.base_url
        )
        context.page = context.browser_context.new_page()

    if "api" in tags:
        context.api_context = context.playwright.request.new_context(
            base_url=context.base_url
        )


def after_scenario(context, scenario):
    for api_context in context.additional_api_contexts:
        api_context.dispose()
    if context.api_context is not None:
        context.api_context.dispose()
    if context.browser_context is not None:
        context.browser_context.close()
    if context.browser is not None:
        context.browser.close()
    context.playwright.stop()
