import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { AccountNotesService, RequestNotesService } from 'cca-api';

export const NotesResolver: ResolveFn<unknown> = (route) => {
  const noteId = +route.paramMap.get('noteId');
  const workflowId = route.paramMap.get('workflowId');

  return workflowId
    ? inject(RequestNotesService).getRequestNote(noteId)
    : inject(AccountNotesService).getAccountNote(noteId);
};
