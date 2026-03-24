import { enableProdMode, provideZoneChangeDetection } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';

import { initAll } from 'govuk-frontend/dist/govuk/govuk-frontend.min.js';

import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, { ...appConfig, providers: [provideZoneChangeDetection(), ...appConfig.providers] });
initAll();
