module.exports = {
  default: {
    requireModule: ['ts-node/register'],
    require: ['features/support/**/*.ts', 'features/step-definitions/**/*.ts'],
    paths: ['features/**/*.feature'],
    format: ['progress', 'json:reports/cucumber-report.json'],
    worldParameters: {
      baseUrl: process.env.BASE_URL || 'https://demowebshop.tricentis.com'
    }
  }
};
