import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { TextareaComponent } from '@netz/govuk-components';
import { PROVIDE_EVIDENCE_SUBTASK } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';
import { generateDownloadUrl } from '@shared/utils';

import {
  PROVIDE_EVIDENCE_DETAILS_FORM,
  ProvideEvidenceDetailsFormProvider,
  UnderlyingAgreementActivationDetailsFormModel,
} from './provide-evidence-details-form.provider';

@Component({
  selector: 'cca-provide-evidence-details',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextareaComponent,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './provide-evidence-details.component.html',
  providers: [ProvideEvidenceDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceDetailsComponent {
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form =
    inject<FormGroup<UnderlyingAgreementActivationDetailsFormModel>>(PROVIDE_EVIDENCE_DETAILS_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    this.taskService
      .saveSubtask(PROVIDE_EVIDENCE_SUBTASK, 'details', this.activatedRoute, {
        evidenceFiles: fileUtils.toUUIDs(this.form.value.evidenceFiles),
        comments: this.form.value.comments,
      })
      .subscribe();
  }
}
