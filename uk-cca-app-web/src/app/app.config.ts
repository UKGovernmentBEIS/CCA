import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { ApplicationConfig, ErrorHandler, importProvidersFrom, inject, provideAppInitializer } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { provideRouter, withInMemoryScrolling, withRouterConfig } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { ConfigService } from '@shared/config';
import { AuthService, GlobalErrorHandlingService, LatestTermsService } from '@shared/services';
import { KeycloakAngularModule, KeycloakOptions, KeycloakService } from 'keycloak-angular';
import { KeycloakConfig } from 'keycloak-js';

import { ApiModule, Configuration } from 'cca-api';

import { environment } from 'src/environments/environment';

import { APP_ROUTES, routerOptions } from './app.routes';
import { HttpErrorInterceptor } from './interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from './interceptors/pending-request.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([HttpErrorInterceptor, PendingRequestInterceptor]),
      withInterceptorsFromDi(), // needed because KeycloakInterceptor is a Class Guard Injected in KeycloakAngularModule
    ),
    provideAppInitializer(() => {
      const initializerFn = init(
        inject(AuthService),
        inject(ConfigService),
        inject(KeycloakService),
        inject(LatestTermsService),
      );
      return initializerFn();
    }),
    importProvidersFrom(
      ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl })),
      KeycloakAngularModule,
    ),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    Title,
    provideRouter(
      APP_ROUTES,
      withRouterConfig(routerOptions),
      withInMemoryScrolling({ scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled' }),
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
        const options: KeycloakOptions = {
          ...environment.keycloakOptions,
          config: {
            ...(environment.keycloakOptions.config as KeycloakConfig),
            url: state.keycloakServerUrl ?? (environment.keycloakOptions.config as KeycloakConfig).url,
          },
        };
        return keycloakService.init(options);
      })
      .catch((error) => console.error(error))
      .then(() => firstValueFrom(authService.checkUser()))
      .then(() => firstValueFrom(latestTermsService.initLatestTerms()))
      .catch((error) => console.error('[APP_INITIALIZE] init Keycloak failed', error));
}
