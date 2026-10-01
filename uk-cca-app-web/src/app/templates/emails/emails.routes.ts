import { Routes } from '@angular/router';

export const EMAILS_ROUTES: Routes = [
  {
    path: ':templateId',
    children: [
      {
        path: '',
        title: 'Email template',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () => import('./email-view/email-view.component').then((m) => m.EmailViewComponent),
      },
      {
        path: 'edit',
        title: 'Edit email template',
        data: { breadcrumb: false, backlink: '../' },
        loadComponent: () => import('./email-edit/email-edit.component').then((m) => m.EmailEditComponent),
      },
    ],
  },
];
