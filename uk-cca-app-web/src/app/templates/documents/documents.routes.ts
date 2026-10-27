import { Routes } from '@angular/router';

import { RouteBacklink } from '@netz/common/navigation';

export const DOCUMENTS_ROUTES: Routes = [
  {
    path: ':templateId',
    children: [
      {
        title: 'Document template',
        path: '',
        loadComponent: () => import('./document-view/document-view.component').then((c) => c.DocumentViewComponent),
        data: {
          breadcrumb: false,
          // Entered from the documents tab or from an email template's attached documents
          // `fromEmail` must be a template id, so a crafted value cannot point the back link elsewhere
          backlink: ((_data, route) => {
            const fromEmail = route.queryParamMap.get('fromEmail');

            return fromEmail && /^\d+$/.test(fromEmail)
              ? { link: `../../email/${fromEmail}`, queryParams: {} }
              : { link: '../../', fragment: 'documents' };
          }) satisfies RouteBacklink,
        },
      },
      {
        title: 'Edit document template',
        path: 'edit',
        loadComponent: () => import('./document-edit/document-edit.component').then((c) => c.DocumentEditComponent),
        data: { breadcrumb: false, backlink: '../' },
      },
      {
        title: 'Your download has started',
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.DocumentTemplatesFileDownloadComponent),
      },
    ],
  },
];
