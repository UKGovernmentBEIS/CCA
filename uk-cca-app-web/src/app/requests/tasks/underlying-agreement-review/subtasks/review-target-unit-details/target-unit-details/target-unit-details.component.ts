import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import {
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TARGET_UNIT_DETAILS_REVIEW_FORM,
  TargetUnitDetailsReviewFormModel,
  TargetUnitDetailsReviewFormProvider,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions } from '@shared/pipes';
import { produce } from 'immer';

import {
  SectorAssociationSchemeService,
  SubsectorAssociationInfoDTO,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementReviewRequestTaskPayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';

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
  providers: [TargetUnitDetailsReviewFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  private readonly sectorAssociationId = this.store.select(underlyingAgreementReviewQuery.selectSectorAssociationId);

  private readonly subsectors = signal<SubsectorAssociationInfoDTO[]>([]);

  protected readonly subsectorsOptions = computed(() =>
    this.subsectors().map((subsector) => ({
      text: subsector.name,
      value: subsector.id,
    })),
  );

  protected readonly form = inject<FormGroup<TargetUnitDetailsReviewFormModel>>(TARGET_UNIT_DETAILS_REVIEW_FORM);

  protected readonly operatorTypeOptions = operatorTypeOptions;

  ngOnInit() {
    if (this.sectorAssociationId()) {
      this.sectorAssociationSchemeService
        .getSectorAssociationSchemeBySectorAssociationId(this.sectorAssociationId())
        .subscribe((scheme) => {
          if (scheme.subsectorAssociations) {
            this.subsectors.set(scheme.subsectorAssociations);
          }
        });
    }
  }

  onSubmit() {
    const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);
    const updatedPayload = updateTUDetails(actionPayload, this.form, this.subsectors());

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
      sectionsCompleted,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
        const path = isTargetUnitDetailsWizardCompleted(
          payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails,
        )
          ? '../decision'
          : `../${ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS}`;

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

function updateTUDetails(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<TargetUnitDetailsReviewFormModel>,
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
