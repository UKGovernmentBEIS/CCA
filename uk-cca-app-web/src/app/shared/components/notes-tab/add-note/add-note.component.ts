import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Observable } from 'rxjs';

import { ButtonDirective, TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AccountNoteRequest, AccountNotesService, RequestNoteRequest, RequestNotesService } from 'cca-api';

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
  private readonly accountNotesService = inject(AccountNotesService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<AddNoteFormModel>(ADD_NOTE_FORM);
  protected readonly downloadUrl = '../file-download/';

  onSubmit() {
    if (this.form.invalid) return;

    const workflowId = this.activatedRoute.snapshot.paramMap.get('workflowId');
    const targetUnitId = this.activatedRoute.snapshot.paramMap.get('targetUnitId');

    const filesValue = this.form.value.files;
    const files = Array.isArray(filesValue) ? filesValue : filesValue ? [filesValue] : [];
    const uuidFilePairs = files?.map((f) => ({ uuid: f?.uuid, file: f?.file })) ?? [];

    let payload: RequestNoteRequest | AccountNoteRequest;
    let request$: Observable<unknown>;

    if (workflowId) {
      payload = {
        note: this.form.value.note,
        files: fileUtils.toUUIDs(uuidFilePairs),
        requestId: workflowId,
      } satisfies RequestNoteRequest;

      request$ = this.requestNotesService.createRequestNote(payload);
    } else if (targetUnitId) {
      payload = {
        note: this.form.value.note,
        files: fileUtils.toUUIDs(uuidFilePairs),
        accountId: +targetUnitId,
      } satisfies AccountNoteRequest;

      request$ = this.accountNotesService.createAccountNote(payload);
    } else {
      throw new Error('could not find correct param');
    }

    request$.subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute, fragment: 'notes' });
    });
  }
}
