import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { userIsRegulatorGuard } from '@shared/guards';

import { AccountNotesService } from 'cca-api';

export const NOTES_ROUTES: Routes = [
  {
    path: 'notes',
    canActivate: [userIsRegulatorGuard],
    children: [
      {
        path: 'add-note',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./add-note/add-note.component').then((c) => c.AddNoteComponent),
      },
      {
        path: 'edit-note/:noteId',
        data: { backlink: '../../../', breadcrumb: false },
        resolve: {
          note: (route: any) => inject(AccountNotesService).getAccountNote(+route.paramMap.get('noteId')),
        },
        loadComponent: () => import('./edit-note/edit-note.component').then((c) => c.EditNoteComponent),
      },
      {
        path: 'delete-note/:noteId',
        data: { backlink: '../../../', breadcrumb: false },

        loadComponent: () => import('./delete-note/delete-note.component').then((c) => c.DeleteNoteComponent),
      },
      {
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.NoteFilesDownloadComponent),
      },
    ],
  },
];
