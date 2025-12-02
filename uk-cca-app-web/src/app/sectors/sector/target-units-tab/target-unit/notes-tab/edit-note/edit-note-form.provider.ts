import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AccountNoteDto, AccountNotesService } from 'cca-api';

export type EditNoteFormModel = FormGroup<{
  note: FormControl<string>;
  files: FormControl<FileUploadEvent[]>;
}>;

export const EDIT_NOTE_FORM = new InjectionToken<EditNoteFormModel>('Edit note form');

export const EditNoteFormProvider: Provider = {
  provide: EDIT_NOTE_FORM,
  deps: [FormBuilder, FileUploadService, AccountNotesService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    fileUploadService: FileUploadService,
    accountNotesService: AccountNotesService,
    activatedRoute: ActivatedRoute,
  ) => {
    const accountId = +activatedRoute.snapshot.paramMap.get('targetUnitId');
    const note = activatedRoute.snapshot.data['note'] as AccountNoteDto | null;

    const attachments = note?.payload?.files ?? {};
    const prepopulatedFiles: FileUploadEvent[] = fileUtils.toFiles(
      Object.keys(attachments),
      attachments,
    ) as FileUploadEvent[];

    const filesControl = fb.control<FileUploadEvent[]>(prepopulatedFiles, {
      asyncValidators: [
        ...createCommonFileAsyncValidators(false),
        fileUploadService.uploadMany((file) =>
          accountNotesService.uploadAccountNoteFile(accountId, file, 'events', true),
        ),
      ],
      updateOn: 'change',
    });

    return fb.group({
      note: fb.control<string>(note?.payload?.note ?? null, [
        GovukValidators.required('Enter a note'),
        GovukValidators.maxLength(10000, 'The note should not be more than 10000 characters'),
      ]),
      files: filesControl,
    });
  },
};
