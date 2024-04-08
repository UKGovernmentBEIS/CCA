import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApplicationRef, DoBootstrap, ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';

import { combineLatest, firstValueFrom } from 'rxjs';

import { initializeGoogleAnalytics } from '@core/analytics';
import { ConfigService } from '@core/config/config.service';
import { AnalyticsInterceptor } from '@core/interceptors/analytics.interceptor';
import { HttpErrorInterceptor } from '@core/interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from '@core/interceptors/pending-request.interceptor';
import { BackLinkComponent, BreadcrumbsComponent } from '@core/navigation';
import { AuthService } from '@core/services/auth.service';
import { GlobalErrorHandlingService } from '@core/services/global-error-handling.service';
import { SharedModule } from '@shared/shared.module';
import { PasswordStrengthMeterComponent } from 'angular-password-strength-meter';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';
import { KeycloakAngularModule, KeycloakOptions, KeycloakService } from 'keycloak-angular';
import { KeycloakConfig } from 'keycloak-js';

import { ApiModule, Configuration } from 'cca-api';

import { environment } from '../environments/environment';
import { AccessibilityComponent } from './accessibility/accessibility.component';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { CookiesContainerComponent } from './cookies/cookies-container.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LegislationComponent } from './legislation/legislation.component';
import { PrivacyNoticeComponent } from './privacy-notice/privacy-notice.component';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TimeoutModule } from './timeout/timeout.module';
import { VersionComponent } from './version/version.component';

@NgModule({
  declarations: [AppComponent],
  imports: [
    AccessibilityComponent,
    AppRoutingModule,
    ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl })),
    BackLinkComponent,
    BreadcrumbsComponent,
    BrowserModule,
    ContactUsComponent,
    CookiesContainerComponent,
    FeedbackComponent,
    KeycloakAngularModule,
    LandingPageComponent,
    LegislationComponent,
    PasswordStrengthMeterComponent,
    PrivacyNoticeComponent,
    SharedModule,
    TermsAndConditionsComponent,
    TimeoutModule,
    VersionComponent,
  ],
  providers: [
    provideZxvbnServiceForPSM(),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    provideHttpClient(withInterceptors([HttpErrorInterceptor, PendingRequestInterceptor, AnalyticsInterceptor])),
    Title,
  ],
})
export class AppModule implements DoBootstrap {
  ngDoBootstrap(appRef: ApplicationRef): void {
    const authService = appRef.injector.get(AuthService);
    const configService = appRef.injector.get(ConfigService);
    const keycloakService = appRef.injector.get(KeycloakService);

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
      .then(() => firstValueFrom(combineLatest([configService.getMeasurementId(), configService.getPropertyId()])))
      .then(([measurementId, propertyId]) => initializeGoogleAnalytics(measurementId, propertyId))
      .then(() => appRef.bootstrap(AppComponent))
      .catch((error) => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}
