import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { userIsRegulatorGuard } from '@shared/guards';

import { RequestNotesService } from 'cca-api';

export const WORKFLOW_NOTES_ROUTES: Routes = [
  {
    path: 'notes',
    canActivate: [userIsRegulatorGuard],
    children: [
      {
        path: 'add-note',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./add-note/add-note.component').then((c) => c.WorkflowAddNoteComponent),
      },
      {
        path: 'edit-note/:noteId',
        data: { backlink: '../../../', breadcrumb: false },
        resolve: {
          note: (route: any) => inject(RequestNotesService).getRequestNote(+route.paramMap.get('noteId')),
        },
        loadComponent: () => import('./edit-note/edit-note.component').then((c) => c.WorkflowEditNoteComponent),
      },
      {
        path: 'delete-note/:noteId',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./delete-note/delete-note.component').then((c) => c.WorkflowDeleteNoteComponent),
      },
      {
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.RequestNoteFilesDownloadComponent),
      },
    ],
  },
];
