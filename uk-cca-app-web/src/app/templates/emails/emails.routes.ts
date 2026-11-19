import { Routes } from '@angular/router';

export const EMAILS_ROUTES: Routes = [
  {
    path: ':templateId',
    children: [
      {
        title: 'Email template',
        path: '',
        loadComponent: () => import('./email-view/email-view.component').then((c) => c.EmailViewComponent),
        data: { breadcrumb: false, backlink: '../../' },
      },
      {
        title: 'Edit email template',
        path: 'edit',
        loadComponent: () => import('./email-edit/email-edit.component').then((c) => c.EmailEditComponent),
        data: { breadcrumb: false, backlink: '../' },
      },
    ],
  },
];
