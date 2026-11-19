import { Routes } from '@angular/router';

export const TEMPLATES_ROUTES: Routes = [
  {
    title: 'Templates',
    path: '',
    children: [
      {
        title: 'Templates',
        path: '',
        loadComponent: () => import('./templates.component').then((c) => c.TemplatesComponent),
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
