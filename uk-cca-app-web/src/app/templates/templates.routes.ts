import { Routes } from '@angular/router';

export const TEMPLATES_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        loadComponent: () => import('./templates.component').then((m) => m.TemplatesComponent),
      },
      {
        path: 'email',
        loadChildren: () => import('./emails/emails.routes').then((r) => r.EMAILS_ROUTES),
      },
      {
        path: 'document',
        loadChildren: () => import('./documents/documents.routes').then((r) => r.DOCUMENTS_ROUTES),
      },
    ],
  },
];
