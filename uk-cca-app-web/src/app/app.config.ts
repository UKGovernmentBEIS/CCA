import { provideHttpClient, withInterceptors, withXhr } from '@angular/common/http';
import { type ApplicationConfig, ErrorHandler, inject, provideAppInitializer } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { provideRouter, TitleStrategy, withInMemoryScrolling, withRouterConfig } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { ConfigService } from '@shared/config';
import {
  AuthService,
  CountryService,
  CountyService,
  GlobalErrorHandlingService,
  KeycloakService,
  LatestTermsService,
} from '@shared/services';
import { logger } from '@shared/utils';
import type { KeycloakServerConfig } from 'keycloak-js';

import { provideCcaApi } from 'cca-api';

import { environment } from 'src/environments/environment';

import { APP_ROUTES, routerOptions } from './app.routes';
import { HttpErrorInterceptor } from './interceptors/http-error.interceptor';
import { KeycloakBearerInterceptor } from './interceptors/keycloak-bearer.interceptor';
import { PendingRequestInterceptor } from './interceptors/pending-request.interceptor';
import { CcaTitleStrategy } from './title-strategy';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withXhr(),
      withInterceptors([KeycloakBearerInterceptor, HttpErrorInterceptor, PendingRequestInterceptor]),
    ),
    provideAppInitializer(() => {
      const initializerFn = init(
        inject(AuthService),
        inject(ConfigService),
        inject(KeycloakService),
        inject(LatestTermsService),
        inject(CountryService),
        inject(CountyService),
      );
      return initializerFn();
    }),
    provideCcaApi({ basePath: environment.apiOptions.baseUrl }),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    Title,
    provideRouter(
      APP_ROUTES,
      withRouterConfig(routerOptions),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
      }),
    ),
    { provide: TitleStrategy, useClass: CcaTitleStrategy },
  ],
};

function init(
  authService: AuthService,
  configService: ConfigService,
  keycloakService: KeycloakService,
  latestTermsService: LatestTermsService,
  countryService: CountryService,
  countyService: CountyService,
) {
  return () =>
    firstValueFrom(configService.initConfigState())
      .then((state) => {
        const keycloakConfig: KeycloakServerConfig = {
          ...environment.keycloakConfig,
          ...environment.keycloakInitOptions,
          url: state.keycloakServerUrl,
        };
        return keycloakService.init(keycloakConfig);
      })
      .catch((error) => logger.error(error))
      .then(() => firstValueFrom(authService.checkUser()))
      .then(() => firstValueFrom(latestTermsService.initLatestTerms()))
      .then(() => Promise.all([firstValueFrom(countryService.load()), firstValueFrom(countyService.load())]))
      .catch((error) => logger.error('[APP_INITIALIZE] init Keycloak failed', error));
}
