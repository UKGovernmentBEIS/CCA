// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import type { KeycloakInitOptions, KeycloakServerConfig } from 'keycloak-js';

// Add here your keycloak setup infos
const keycloakConfig: Omit<KeycloakServerConfig, 'url'> = {
  realm: 'uk-pmrv',
  clientId: 'uk-cca-web-app',
};

const keycloakInitOptions: KeycloakInitOptions = {
  onLoad: 'check-sso',
  enableLogging: false,
  pkceMethod: 'S256',
};

const apiOptions = {
  baseUrl: '/api',
};

const timeoutBanner = {
  timeOffsetSeconds: 30,
};

const serviceName = 'Climate Change Agreements';
const serviceNameShort = 'CCA';

export const environment = {
  production: true,
  keycloakConfig,
  keycloakInitOptions,
  apiOptions,
  timeoutBanner,
  serviceName,
  serviceNameShort,
};
