import { Routes } from '@angular/router';

export const DOCUMENTS_ROUTES: Routes = [
  {
    path: ':templateId',
    children: [
      {
        path: '',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () => import('./document-view/document-view.component').then((m) => m.DocumentViewComponent),
      },
      {
        path: 'edit',
        data: { breadcrumb: false, backlink: '../' },
        loadComponent: () => import('./document-edit/document-edit.component').then((m) => m.DocumentEditComponent),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.DocumentTemplatesFileDownloadComponent),
      },
    ],
  },
];
