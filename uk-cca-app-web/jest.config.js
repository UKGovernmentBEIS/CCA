const { pathsToModuleNameMapper } = require('ts-jest');
const { paths } = require('./tsconfig.json').compilerOptions;
const { defaultTransformerOptions } = require('jest-preset-angular/presets');

/** @type {import('ts-jest/dist/types').InitialOptionsTsJest} */
module.exports = {
  preset: 'jest-preset-angular',
  cacheDirectory: 'tmp/jest/cache',
  moduleNameMapper: {
    '^govuk-components': '<rootDir>/dist/govuk-components/esm2022/public-api.mjs',
    '^cca-api': '<rootDir>/dist/cca-api/esm2022/cca-api.mjs',
    ...pathsToModuleNameMapper(paths, { prefix: '<rootDir>/' }),
    'html-diff(.*)': '<rootDir>/html-diff$1.js',
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
