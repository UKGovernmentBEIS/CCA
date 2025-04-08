import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  AuthorisationAdditionalEvidenceWizardStep,
} from '../../../underlying-agreement.types';
import {
  PROVIDE_EVIDENCE_FORM,
  ProvideEvidenceFormModel,
  ProvideEvidenceFormProvider,
} from './provide-evidence-form.provider';

@Component({
  selector: 'cca-provide-evidence',
  standalone: true,
  imports: [MultipleFileInputComponent, ReactiveFormsModule, WizardStepComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './provide-evidence.component.html',
  providers: [ProvideEvidenceFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideEvidenceComponent {
  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    this.taskService
      .saveSubtask(
        AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
        AuthorisationAdditionalEvidenceWizardStep.PROVIDE_EVIDENCE,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
