import { Routes } from '@angular/router';

import { BusinessErrorComponent } from './business-error/business-error.component';
import { InternalServerErrorComponent } from './internal-server-error/internal-server-error.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

export const ERROR_ROUTES: Routes = [
  {
    path: '500',
    data: { pageTitle: 'Sorry, there is a problem with the service' },
    component: InternalServerErrorComponent,
  },
  {
    path: 'business',
    component: BusinessErrorComponent,
  },
  {
    path: '404',
    data: { pageTitle: 'Page not found' },
    component: PageNotFoundComponent,
  },
];
