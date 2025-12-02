import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ButtonDirective, TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { RequestNoteRequest, RequestNotesService } from 'cca-api';

import { ADD_NOTE_FORM, AddNoteFormModel, AddNoteFormProvider } from './add-note-form.provider';

@Component({
  selector: 'cca-workflow-add-note',
  imports: [ReactiveFormsModule, TextareaComponent, MultipleFileInputComponent, ButtonDirective, RouterLink],
  templateUrl: './add-note.component.html',
  providers: [AddNoteFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowAddNoteComponent {
  private readonly requestNotesService = inject(RequestNotesService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<AddNoteFormModel>(ADD_NOTE_FORM);
  protected readonly downloadUrl = '../file-download/';

  onSubmit() {
    if (this.form.invalid) return;

    const requestId = this.activatedRoute.snapshot.paramMap.get('workflowId');
    const filesValue = this.form.value.files;
    const files = Array.isArray(filesValue) ? filesValue : filesValue ? [filesValue] : [];
    const uuidFilePairs = files?.map((f) => ({ uuid: f?.uuid, file: f?.file })) ?? [];

    const payload: RequestNoteRequest = {
      requestId,
      note: this.form.value.note,
      files: fileUtils.toUUIDs(uuidFilePairs),
    };

    this.requestNotesService.createRequestNote(payload).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute, fragment: 'notes' });
    });
  }
}
