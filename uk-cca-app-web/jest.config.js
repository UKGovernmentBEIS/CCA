const { createCjsPreset } = require('jest-preset-angular/presets/index.js');

const jestPresetConfig = createCjsPreset();

/** @type {import('ts-jest/dist/types').JestConfigWithTsJest} */
module.exports = {
  ...jestPresetConfig,
  injectGlobals: true,
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  cacheDirectory: 'tmp/jest/cache',
  testTimeout: 30000,
  moduleNameMapper: {
    '^@netz/govuk-components': '<rootDir>/dist/govuk-components/fesm2022/netz-govuk-components.mjs',
    '^cca-api': '<rootDir>/dist/cca-api/fesm2022/cca-api.mjs',
    '^@netz/common$': '<rootDir>/dist/common/fesm2022/netz-common.mjs',
    '^@netz/common/testing$': '<rootDir>/projects/common/testing',
    '^@netz/common/testing/marble-helpers$': '<rootDir>/projects/common/testing/marble-helpers.ts',
    '^@netz/common/(.*)$': '<rootDir>/dist/common/fesm2022/netz-common-$1.mjs',
    '^@testing$': '<rootDir>/src/testing/index.ts',
    '^@error/(.*)': '<rootDir>/src/app/error/$1',
    '^@shared/(.*)': '<rootDir>/src/app/shared/$1',
    '^@requests/(.*)': '<rootDir>/src/app/requests/$1',
  },
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
  modulePathIgnorePatterns: ['<rootDir>/projects/.*/package\\.json$'],
  transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)'],
};
