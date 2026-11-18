import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus, TasksApiService } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { cca3MigrationAccountActivationQuery } from '../../../+state/cca3-migration-account-activation.selectors';
import { createSaveActionDTO } from '../../../transform';
import { CCA3MigrationRequestTaskPayload } from '../../../types';
import {
  CCA3_MIGRATION_PROVIDE_EVIDENCE_DETAILS_FORM,
  Cca3MigrationAccountActivationDetailsFormModel,
  ProvideEvidenceDetailsFormProvider,
} from './provide-evidence-details-form.provider';

@Component({
  selector: 'cca-provide-evidence-details',
  templateUrl: './provide-evidence-details.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextareaComponent,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideEvidenceDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly form = inject<Cca3MigrationAccountActivationDetailsFormModel>(
    CCA3_MIGRATION_PROVIDE_EVIDENCE_DETAILS_FORM,
  );

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const payload = this.store.select(cca3MigrationAccountActivationQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const sectionsCompleted = produce(
      this.store.select(cca3MigrationAccountActivationQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[CCA3_MIGRATION_PROVIDE_EVIDENCE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
      },
    );

    const dto = createSaveActionDTO(Number(this.taskId), updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: CCA3MigrationRequestTaskPayload,
  form: Cca3MigrationAccountActivationDetailsFormModel,
): CCA3MigrationRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.activationDetails = {
      evidenceFiles: fileUtils.toUUIDs(form.value.evidenceFiles),
      comments: form.value.comments,
    };

    draft.activationAttachments = fileUtils.toAttachments(form.value.evidenceFiles);
  });
}
