import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';
import { ADDITIONAL_INFO_FORM, AdditionalInfoFormModel, provideAdditionalInfo } from './additional-info.provider';

@Component({
  selector: 'cca-explanation-component',
  standalone: true,
  template: `
    <cca-wizard-step
      [formGroup]="form"
      [caption]="caption"
      heading="Provide any additional information here to support your decision (optional)"
      (formSubmit)="submit()"
    >
      <div govuk-textarea formControlName="additionalInfo" hint="This will be included in the official notice."></div>
      <cca-multiple-file-input [baseDownloadUrl]="downloadUrl" formControlName="files"></cca-multiple-file-input>
    </cca-wizard-step>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [provideAdditionalInfo()],
})
export class AdditionalInfoComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<AdditionalInfoFormModel>(ADDITIONAL_INFO_FORM);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  protected readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  submit() {
    const files = this.form.value.files.map((f) => f.uuid);
    const underlyingAgreementReviewTaskService = this.taskService as UnderlyingAgreementVariationReviewTaskService;

    underlyingAgreementReviewTaskService
      .saveReviewDetermination({
        additionalInformation: this.form.value.additionalInfo,
        files,
      })
      .subscribe(() =>
        this.router.navigate(['../', OverallDecisionWizardStep.CHECK_ANSWERS], {
          relativeTo: this.activatedRoute,
          queryParamsHandling: 'preserve',
        }),
      );
  }
}
