import { Routes } from '@angular/router';

import { userIsRegulatorGuard } from '@shared/guards';

import { NotesResolver } from './notes.resolver';

export const NOTES_ROUTES: Routes = [
  {
    path: 'notes',
    canActivate: [userIsRegulatorGuard],
    children: [
      {
        path: 'add-note',
        title: 'Add a note',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('./add-note/add-note.component').then((c) => c.WorkflowAddNoteComponent),
      },
      {
        path: 'edit-note/:noteId',
        title: 'Edit note',
        data: { backlink: '../../../', breadcrumb: false },
        resolve: { note: NotesResolver },
        loadComponent: () => import('./edit-note/edit-note.component').then((c) => c.WorkflowEditNoteComponent),
      },
      {
        path: 'delete-note/:noteId',
        title: 'Are you sure you want to delete this note?',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./delete-note/delete-note.component').then((c) => c.WorkflowDeleteNoteComponent),
      },
      {
        path: 'file-download/:uuid',
        title: 'Your download has started',
        loadComponent: () => import('@shared/components').then((c) => c.RequestNoteFilesDownloadComponent),
      },
    ],
  },
];
