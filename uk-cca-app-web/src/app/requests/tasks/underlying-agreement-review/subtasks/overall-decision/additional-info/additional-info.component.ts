import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { TasksApiService, underlyingAgreementReviewQuery } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { createSaveDeterminationActionDTO } from '../../../transform';
import { resetDeterminationStatus } from '../../../utils';
import { ADDITIONAL_INFO_FORM, AdditionalInfoFormModel, provideAdditionalInfo } from './additional-info.provider';

@Component({
  selector: 'cca-explanation-component',
  template: `
    <cca-wizard-step
      [formGroup]="form"
      [caption]="caption"
      heading="Provide any additional information here to support your decision (optional)"
      (formSubmit)="submit()"
    >
      <div govuk-textarea formControlName="additionalInfo" hint="This will be included in the official notice."></div>
      <cca-multiple-file-input [baseDownloadUrl]="downloadUrl" formControlName="files" />
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [provideAdditionalInfo()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalInfoComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<AdditionalInfoFormModel>(ADDITIONAL_INFO_FORM);

  protected readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  submit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const currReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = resetDeterminationStatus(currReviewSectionsCompleted);

    const determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
    const files = this.form.value.files.map((f) => f.uuid);

    const payload = createSaveDeterminationActionDTO(
      requestTaskId,
      { ...determination, additionalInformation: this.form.value.additionalInfo, files },
      reviewSectionsCompleted,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      });
    });
  }
}
