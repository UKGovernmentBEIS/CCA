import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import {
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import { ADDITIONAL_INFO_FORM, AdditionalInfoFormModel, provideAdditionalInfo } from './additional-info.provider';

@Component({
  selector: 'cca-explanation-component',
  templateUrl: './additional-info.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    DetailsComponent,
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

  protected readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly impactsAgreementCtrlValue = toSignal(this.form.controls.variationImpactsAgreement.valueChanges, {
    initialValue: this.form.controls.variationImpactsAgreement.value,
  });

  protected readonly infoLabel = computed(() => (this.impactsAgreementCtrlValue() ? '(optional)' : ''));

  protected readonly infoHint = computed(() =>
    this.impactsAgreementCtrlValue() ? '' : 'Enter a brief description of the changes you have accepted. ',
  );

  submit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[OPERATOR_ASSENT_DECISION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const files = this.form.value.files.map((f) => f.uuid);
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...determination,
      variationImpactsAgreement: this.form.value.variationImpactsAgreement,
      additionalInformation: this.form.value.additionalInformation,
      files,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
