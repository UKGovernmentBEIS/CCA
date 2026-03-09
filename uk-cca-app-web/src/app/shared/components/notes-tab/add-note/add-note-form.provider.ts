import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';

import { AccountNotesService, RequestNotesService } from 'cca-api';

export type AddNoteFormModel = FormGroup<{
  note: FormControl<string>;
  files: FormControl<FileUploadEvent[]>;
}>;

export const ADD_NOTE_FORM = new InjectionToken<AddNoteFormModel>('Add workflow note form');

export const AddNoteFormProvider: Provider = {
  provide: ADD_NOTE_FORM,
  deps: [FormBuilder, FileUploadService, RequestNotesService, AccountNotesService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    fileUploadService: FileUploadService,
    requestNotesService: RequestNotesService,
    accountNotesService: AccountNotesService,
    activatedRoute: ActivatedRoute,
  ) => {
    const workflowId = activatedRoute.snapshot.paramMap.get('workflowId');
    const targetUnitId = activatedRoute.snapshot.paramMap.get('targetUnitId');

    const filesControl = fb.control<FileUploadEvent[]>(null, {
      asyncValidators: [
        ...createCommonFileAsyncValidators(false),
        fileUploadService.uploadMany((file) =>
          workflowId
            ? requestNotesService.uploadRequestNoteFile(workflowId, file, 'events', true)
            : accountNotesService.uploadAccountNoteFile(+targetUnitId, file, 'events', true),
        ),
      ],
      updateOn: 'change',
    });

    return fb.group({
      note: fb.control<string>(null, [
        GovukValidators.required('Enter a note'),
        GovukValidators.maxLength(10000, 'The note should not be more than 10000 characters'),
      ]),
      files: filesControl,
    });
  },
};
