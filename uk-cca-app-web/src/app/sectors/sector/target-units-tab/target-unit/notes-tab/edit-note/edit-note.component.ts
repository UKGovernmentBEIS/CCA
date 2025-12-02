import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AccountNotesService, NoteRequest } from 'cca-api';

import { EDIT_NOTE_FORM, EditNoteFormModel, EditNoteFormProvider } from './edit-note-form.provider';

@Component({
  selector: 'cca-edit-note',
  templateUrl: './edit-note.component.html',
  imports: [ReactiveFormsModule, TextareaComponent, MultipleFileInputComponent, RouterLink],
  providers: [EditNoteFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditNoteComponent {
  private readonly accountNotesService = inject(AccountNotesService);
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

    this.accountNotesService.updateAccountNote(this.noteId, payload).subscribe(() => {
      this.router.navigate(['../../../'], {
        relativeTo: this.activatedRoute,
        fragment: 'notes',
        queryParamsHandling: 'merge',
      });
    });
  }
}
