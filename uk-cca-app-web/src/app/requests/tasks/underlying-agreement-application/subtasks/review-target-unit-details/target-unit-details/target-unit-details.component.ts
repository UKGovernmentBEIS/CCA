import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { take } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import {
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TARGET_UNIT_DETAILS_SUBMIT_FORM,
  TargetUnitDetailsSubmitFormModel,
  TargetUnitDetailsSubmitFormProvider,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions, transformOperatorType } from '@shared/pipes';
import { produce } from 'immer';

import {
  SectorAssociationSchemeService,
  SubsectorAssociationInfoDTO,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-target-unit-details',
  templateUrl: './target-unit-details.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    SelectComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [TargetUnitDetailsSubmitFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);

  private readonly subsectors = signal<SubsectorAssociationInfoDTO[]>([]);

  protected readonly subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.name,
      value: subsector.id,
    })),
  );

  protected readonly transformOperatorType = transformOperatorType;
  protected readonly operatorTypeOptions = operatorTypeOptions;

  protected readonly form = inject<FormGroup<TargetUnitDetailsSubmitFormModel>>(TARGET_UNIT_DETAILS_SUBMIT_FORM);

  ngOnInit() {
    if (this.sectorAssociationId()) {
      this.sectorAssociationSchemeService
        .getSectorAssociationSchemeBySectorAssociationId(this.sectorAssociationId())
        .pipe(take(1))
        .subscribe((scheme) => {
          if (scheme.subsectorAssociations) this.subsectors.set(scheme.subsectorAssociations);
        });
    }
  }

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateTUDetails(actionPayload, this.form, this.subsectors());

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const path = isTargetUnitDetailsWizardCompleted(
          payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
        )
          ? '../check-your-answers'
          : `../${ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS}`;

        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}

function updateTUDetails(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<TargetUnitDetailsSubmitFormModel>,
  subSectors: SubsectorAssociationInfoDTO[],
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails = {
      ...draft.underlyingAgreementTargetUnitDetails,
      ...form.getRawValue(),
      subsectorAssociationName: subSectors.find(
        (subsector) => subsector.id === form.controls.subsectorAssociationId?.value,
      )?.name,
    };
  });
}
