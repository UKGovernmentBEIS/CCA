const { defaultTransformerOptions } = require('jest-preset-angular/presets');

/** @type {import('ts-jest/dist/types').JestConfigWithTsJest} */
module.exports = {
  preset: 'jest-preset-angular',
  cacheDirectory: 'tmp/jest/cache',
  moduleNameMapper: {
    '^@netz/govuk-components': '<rootDir>/dist/govuk-components/esm2022/public-api.mjs',
    '^cca-api': '<rootDir>/dist/cca-api/esm2022/',
    '^@netz/common/(.*)$': '<rootDir>/dist/common/esm2022/$1',
    '^@core/(.*)': '<rootDir>/src/app/core/$1',
    '^@error/(.*)': '<rootDir>/src/app/error/$1',
    '^@shared/(.*)': '<rootDir>/src/app/shared/$1',
    '^@requests/(.*)': '<rootDir>/src/app/requests/$1',
    '^lodash-es$': 'lodash',
  },
  modulePathIgnorePatterns: ['<rootDir>/dist'],
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  moduleFileExtensions: ['ts', 'html', 'js', 'json', 'mjs'],
  transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)'],
  transform: {
    '^.+\\.(ts|js|mjs|html|svg)$': [
      'jest-preset-angular',
      {
        ...defaultTransformerOptions,
        isolatedModules: true,
      },
    ],
  },
};
