import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  type ApplicationConfig,
  ErrorHandler,
  importProvidersFrom,
  inject,
  provideAppInitializer,
} from '@angular/core';
import { Title } from '@angular/platform-browser';
import { provideRouter, withInMemoryScrolling, withRouterConfig } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { ConfigService } from '@shared/config';
import { AuthService, GlobalErrorHandlingService, KeycloakService, LatestTermsService } from '@shared/services';
import type { KeycloakConfig } from 'keycloak-js';

import { ApiModule, Configuration } from 'cca-api';

import { environment } from 'src/environments/environment';

import { APP_ROUTES, routerOptions } from './app.routes';
import { HttpErrorInterceptor } from './interceptors/http-error.interceptor';
import { KeycloakBearerInterceptor } from './interceptors/keycloak-bearer.interceptor';
import { PendingRequestInterceptor } from './interceptors/pending-request.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([KeycloakBearerInterceptor, HttpErrorInterceptor, PendingRequestInterceptor])),
    provideAppInitializer(() => {
      const initializerFn = init(
        inject(AuthService),
        inject(ConfigService),
        inject(KeycloakService),
        inject(LatestTermsService),
      );
      return initializerFn();
    }),
    importProvidersFrom(ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl }))),
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
  ],
};

function init(
  authService: AuthService,
  configService: ConfigService,
  keycloakService: KeycloakService,
  latestTermsService: LatestTermsService,
) {
  return () =>
    firstValueFrom(configService.initConfigState())
      .then((state) => {
        const keycloakConfig: KeycloakConfig = {
          ...environment.keycloakConfig,
          ...environment.keycloakInitOptions,
          url: state.keycloakServerUrl,
        };
        return keycloakService.init(keycloakConfig);
      })
      .catch((error) => console.error(error))
      .then(() => firstValueFrom(authService.checkUser()))
      .then(() => firstValueFrom(latestTermsService.initLatestTerms()))
      .catch((error) => console.error('[APP_INITIALIZE] init Keycloak failed', error));
}
