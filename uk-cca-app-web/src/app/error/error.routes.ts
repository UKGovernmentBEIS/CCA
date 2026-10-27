import { Routes } from '@angular/router';

export const ERROR_ROUTES: Routes = [
  {
    title: 'Sorry, there is a problem with the service',
    path: '500',
    loadComponent: () =>
      import('./internal-server-error/internal-server-error.component').then((c) => c.InternalServerErrorComponent),
  },
  {
    title: 'Business error',
    path: 'business',
    loadComponent: () => import('./business-error/business-error.component').then((c) => c.BusinessErrorComponent),
  },
  {
    title: 'Page not found',
    path: '404',
    loadComponent: () => import('./page-not-found/page-not-found.component').then((c) => c.PageNotFoundComponent),
  },
];
