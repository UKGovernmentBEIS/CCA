import { Routes } from '@angular/router';

import { userIsRegulatorGuard } from '@shared/guards';

import { NotesResolver } from './notes.resolver';

export const NOTES_ROUTES: Routes = [
  {
    path: 'notes',
    canActivate: [userIsRegulatorGuard],
    children: [
      {
        title: 'Add a note',
        path: 'add-note',
        loadComponent: () => import('./add-note/add-note.component').then((c) => c.WorkflowAddNoteComponent),
        data: { backlink: { link: '../../', fragment: 'notes' }, breadcrumb: false },
      },
      {
        title: 'Edit note',
        path: 'edit-note/:noteId',
        loadComponent: () => import('./edit-note/edit-note.component').then((c) => c.WorkflowEditNoteComponent),
        data: { backlink: { link: '../../../', fragment: 'notes' }, breadcrumb: false },
        resolve: { note: NotesResolver },
      },
      {
        title: 'Are you sure you want to delete this note?',
        path: 'delete-note/:noteId',
        loadComponent: () => import('./delete-note/delete-note.component').then((c) => c.WorkflowDeleteNoteComponent),
        data: { backlink: { link: '../../../', fragment: 'notes' }, breadcrumb: false },
      },
      {
        title: 'Your download has started',
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.RequestNoteFilesDownloadComponent),
      },
    ],
  },
];
