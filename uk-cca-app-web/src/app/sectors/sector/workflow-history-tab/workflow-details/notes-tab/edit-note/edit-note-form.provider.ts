import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { RequestNoteDto, RequestNotesService } from 'cca-api';

export type EditNoteFormModel = FormGroup<{
  note: FormControl<string>;
  files: FormControl<FileUploadEvent[]>;
}>;

export const EDIT_NOTE_FORM = new InjectionToken<EditNoteFormModel>('Edit workflow note form');

export const EditNoteFormProvider: Provider = {
  provide: EDIT_NOTE_FORM,
  deps: [FormBuilder, FileUploadService, RequestNotesService, ActivatedRoute],
  useFactory: (
    fb: FormBuilder,
    fileUploadService: FileUploadService,
    requestNotesService: RequestNotesService,
    activatedRoute: ActivatedRoute,
  ) => {
    const requestId = activatedRoute.snapshot.paramMap.get('workflowId');
    const note = activatedRoute.snapshot.data['note'] as RequestNoteDto | null;

    const attachments = note?.payload?.files ?? {};
    const prepopulatedFiles: FileUploadEvent[] = fileUtils.toFiles(
      Object.keys(attachments),
      attachments,
    ) as FileUploadEvent[];

    const filesControl = fb.control<FileUploadEvent[]>(prepopulatedFiles, {
      asyncValidators: [
        ...createCommonFileAsyncValidators(false),
        fileUploadService.uploadMany((file) =>
          requestNotesService.uploadRequestNoteFile(requestId, file, 'events', true),
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
