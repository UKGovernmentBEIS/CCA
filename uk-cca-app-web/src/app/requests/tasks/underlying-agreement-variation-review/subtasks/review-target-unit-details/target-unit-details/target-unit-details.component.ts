import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  isTargetUnitDetailsWizardCompleted,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
  TARGET_UNIT_DETAILS_REVIEW_FORM,
  TargetUnitDetailsReviewFormModel,
  TargetUnitDetailsReviewFormProvider,
  TasksApiService,
  transformAccountReferenceData,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { operatorTypeOptions } from '@shared/pipes';
import { produce } from 'immer';

import {
  SectorAssociationSchemeService,
  SubsectorAssociationInfoDTO,
  UnderlyingAgreementVariationReviewRequestTaskPayload,
  UnderlyingAgreementVariationReviewSavePayload,
} from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects, deleteDecision } from '../../../utils';

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
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const originalAccountReferenceData = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const selectedSubsectorAssociationName = this.subsectors().find(
      (subsector) => subsector.id === this.form.controls.subsectorAssociationId?.value,
    )?.name;

    const updatedPayload = update(actionPayload, this.form, selectedSubsectorAssociationName);

    const originalTUDetails = transformAccountReferenceData(originalAccountReferenceData);
    const currentTUDetails = updatedPayload.underlyingAgreementTargetUnitDetails;

    const areIdentical = areEntitiesIdentical(
      filterFieldsWithFalsyValues(currentTUDetails),
      filterFieldsWithFalsyValues(originalTUDetails),
    );

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? currentDecisions : deleteDecision(currentDecisions, 'TARGET_UNIT_DETAILS');

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.store.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationReviewRequestTaskPayload) => {
        let path = '';

        if (areIdentical) {
          path = '../check-your-answers';
        } else {
          path = isTargetUnitDetailsWizardCompleted(payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails)
            ? '../decision'
            : `../${ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS}`;
        }

        this.router.navigate([path], { relativeTo: this.activatedRoute });
      });
  }
}

function update(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: FormGroup<TargetUnitDetailsReviewFormModel>,
  selectedSubsectorAssociationName: string,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorName = form.getRawValue().operatorName;
    draft.underlyingAgreementTargetUnitDetails.operatorType = form.getRawValue().operatorType;
    draft.underlyingAgreementTargetUnitDetails.subsectorAssociationId = form.getRawValue().subsectorAssociationId;
    draft.underlyingAgreementTargetUnitDetails.subsectorAssociationName = selectedSubsectorAssociationName;
  });
}
