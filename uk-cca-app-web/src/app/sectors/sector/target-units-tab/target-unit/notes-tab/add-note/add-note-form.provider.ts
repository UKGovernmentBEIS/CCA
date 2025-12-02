import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';

import { AccountNotesService } from 'cca-api';

export type AddNoteFormModel = FormGroup<{
  note: FormControl<string>;
  files: FormControl<FileUploadEvent[]>;
}>;

export const ADD_NOTE_FORM = new InjectionToken<AddNoteFormModel>('Add note form');

export const AddNoteFormProvider: Provider = {
  provide: ADD_NOTE_FORM,
  deps: [FormBuilder, FileUploadService, AccountNotesService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    fileUploadService: FileUploadService,
    accountNotesService: AccountNotesService,
    activatedRoute: ActivatedRoute,
  ) => {
    const accountId = +activatedRoute.snapshot.paramMap.get('targetUnitId');
    const filesControl = fb.control<FileUploadEvent[]>(null, {
      asyncValidators: [
        ...createCommonFileAsyncValidators(false),
        fileUploadService.uploadMany((file) =>
          accountNotesService.uploadAccountNoteFile(accountId, file, 'events', true),
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
