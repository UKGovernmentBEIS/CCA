import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NoteRequest, RequestNotesService } from 'cca-api';

import { EDIT_NOTE_FORM, EditNoteFormModel, EditNoteFormProvider } from './edit-note-form.provider';

@Component({
  selector: 'cca-workflow-edit-note',
  templateUrl: './edit-note.component.html',
  imports: [ReactiveFormsModule, TextareaComponent, MultipleFileInputComponent, RouterLink],
  providers: [EditNoteFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowEditNoteComponent {
  private readonly requestNotesService = inject(RequestNotesService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly noteId = +this.activatedRoute.snapshot.paramMap.get('noteId');
  protected readonly form = inject<EditNoteFormModel>(EDIT_NOTE_FORM);

  onSubmit() {
    if (this.form.invalid || !this.noteId) return;

    const filesValue = this.form.value.files;
    const files = Array.isArray(filesValue) ? filesValue : filesValue ? [filesValue] : [];
    const payload: NoteRequest = {
      note: this.form.value.note,
      files: fileUtils.toUUIDs(files as any),
    };

    this.requestNotesService.updateRequestNote(this.noteId, payload).subscribe(() => {
      this.router.navigate(['../../../'], {
        relativeTo: this.activatedRoute,
        fragment: 'notes',
        queryParamsHandling: 'merge',
      });
    });
  }
}
