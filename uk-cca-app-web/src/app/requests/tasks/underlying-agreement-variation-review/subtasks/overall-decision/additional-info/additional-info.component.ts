import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveDeterminationActionDTO } from '../../../transform';
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

  protected readonly form = inject<AdditionalInfoFormModel>(ADDITIONAL_INFO_FORM);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  protected readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  submit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const files = this.form.value.files.map((f) => f.uuid);

    const updatedDetermination = produce(this.determination, (draft) => {
      draft.additionalInformation = this.form.value.additionalInfo;
      draft.files = files;
    });

    const dto = createSaveDeterminationActionDTO(requestTaskId, updatedDetermination, reviewSectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() =>
      this.router.navigate(['../', 'check-your-answers'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      }),
    );
  }
}
