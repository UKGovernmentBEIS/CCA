import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { APP_INITIALIZER, ApplicationConfig, ErrorHandler, importProvidersFrom } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { provideRouter, withRouterConfig } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { initializeGoogleAnalytics } from '@core/analytics';
import { ConfigService } from '@core/config/config.service';
import { AnalyticsInterceptor } from '@core/interceptors/analytics.interceptor';
import { HttpErrorInterceptor } from '@core/interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from '@core/interceptors/pending-request.interceptor';
import { AuthService } from '@core/services/auth.service';
import { GlobalErrorHandlingService } from '@core/services/global-error-handling.service';
import { LatestTermsService } from '@core/services/latest-terms.service';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';
import { KeycloakAngularModule, KeycloakOptions, KeycloakService } from 'keycloak-angular';
import { KeycloakConfig } from 'keycloak-js';

import { ApiModule, Configuration } from 'cca-api';

import { environment } from 'src/environments/environment';

import { APP_ROUTES, routerOptions } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([HttpErrorInterceptor, PendingRequestInterceptor, AnalyticsInterceptor]),
      withInterceptorsFromDi(), // needed because KeycloakInterceptor is a Class Guard Injected in KeycloakAngularModule
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: init,
      multi: true,
      deps: [AuthService, ConfigService, KeycloakService, LatestTermsService],
    },
    importProvidersFrom(
      ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl })),
      KeycloakAngularModule,
    ),
    provideZxvbnServiceForPSM(),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    Title,
    provideRouter(APP_ROUTES, withRouterConfig(routerOptions)),
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
      .then(() => initializeGoogleAnalytics(configService.getMeasurementId(), configService.getPropertyId()))
      .catch((error) => console.error('[APP_INITIALIZE] init Keycloak failed', error));
}
