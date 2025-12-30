import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { VariationDetermination } from 'cca-api';

import { createSaveDeterminationActionDTO } from '../../../transform';
import { ADDITIONAL_INFO_FORM, AdditionalInfoFormModel, provideAdditionalInfo } from './additional-info.provider';

@Component({
  selector: 'cca-explanation-component',
  templateUrl: './additional-info.component.html',
  imports: [
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
    RadioOptionComponent,
    RadioComponent,
    WizardStepComponent,
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

  private readonly variationImpactsAgreementValue = toSignal(
    this.form.controls.variationImpactsAgreement.valueChanges,
    {
      initialValue: this.form.value.variationImpactsAgreement,
    },
  );

  private readonly determination = this.requestTaskStore.select(
    underlyingAgreementVariationReviewQuery.selectDetermination,
  );

  protected readonly isAccepted = computed(() => this.determination().type === 'ACCEPTED');
  protected readonly caption = computed(() => (this.isAccepted() ? 'Accept' : 'Reject'));

  protected readonly showNoChangesGuidance = computed(
    () => this.isAccepted() && this.variationImpactsAgreementValue() === false,
  );

  protected readonly additionalInfoLabel = computed(() => {
    const optionalSuffix =
      (this.isAccepted() && this.variationImpactsAgreementValue() === true) || !this.isAccepted() ? ' (optional)' : '';
    return `Add any additional information${optionalSuffix}`;
  });

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

    const updatedDetermination = update(this.determination(), this.form);
    const dto = createSaveDeterminationActionDTO(requestTaskId, updatedDetermination, reviewSectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() =>
      this.router.navigate(['../', 'check-your-answers'], {
        relativeTo: this.activatedRoute,
        queryParamsHandling: 'preserve',
      }),
    );
  }
}

function update(determination: VariationDetermination, form: AdditionalInfoFormModel): VariationDetermination {
  return produce(determination, (draft) => {
    if (determination.type === 'ACCEPTED') {
      draft.variationImpactsAgreement = form.value.variationImpactsAgreement;
    } else {
      // ensure we don't send an irrelevant value when rejected
      delete draft.variationImpactsAgreement;
    }

    draft.additionalInformation = form.value.additionalInformation;
    draft.files = fileUtils.toUUIDs(form.value.files);
  });
}
