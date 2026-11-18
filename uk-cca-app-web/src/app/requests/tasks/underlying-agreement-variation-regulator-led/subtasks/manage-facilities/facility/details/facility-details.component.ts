import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  applySchemeVersionsSideEffect,
  FacilityWizardStep,
  isCCA2FacilityWizardCompleted,
  isCCA3FacilityWizardCompleted,
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
  VariationFacilityDetailsFormComponent,
  VariationFacilityDetailsFormModel,
  VariationFacilityDetailsFormProvider,
} from '@requests/common';
import { produce } from 'immer';

import {
  Facility,
  UnderlyingAgreementVariationRegulatorLedSavePayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-facility-details',
  template: `<cca-variation-facility-details-form (submitChange)="onSubmit($event)" />`,
  imports: [VariationFacilityDetailsFormComponent],
  providers: [VariationFacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit(form: FormGroup<VariationFacilityDetailsFormModel>) {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    let updatedPayload = updateFacilityDetails(actionPayload, form, this.facilityId);

    updatedPayload = applySchemeVersionsSideEffect(
      updatedPayload,
      this.facilityId,
    ) as UnderlyingAgreementVariationRegulatorLedSavePayload;

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationSubmitRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        this.router.navigate(nextRoute(facility), { relativeTo: this.activatedRoute });
      });
  }
}

function updateFacilityDetails(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  form: FormGroup<VariationFacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const participatingSchemeVersions = form.value.participatingSchemeVersions;
    const formRawValue = form.getRawValue();

    draft.facilities[facilityIndex].facilityDetails = {
      ...draft.facilities[facilityIndex].facilityDetails,
      name: form.controls.name.value,
      participatingSchemeVersions,
      isCoveredByUkets: form.value.isCoveredByUkets,
      uketsId: form.value.uketsId,
      applicationReason: formRawValue.applicationReason,
      previousFacilityId: formRawValue.previousFacilityId,
      facilityAddress: formRawValue.facilityAddress,
    };
  });
}

function nextRoute(facility: Facility): string[] {
  const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions;

  if (isCCA3Scheme(schemeVersions)) {
    if (isCCA3FacilityWizardCompleted(facility)) return ['../check-your-answers'];

    return isCCA2FacilityWizardCompleted(facility)
      ? ['../', FacilityWizardStep.TARGET_COMPOSITION]
      : ['../', FacilityWizardStep.CONTACT_DETAILS];
  }

  return isCCA2FacilityWizardCompleted(facility)
    ? ['../check-your-answers']
    : ['../', FacilityWizardStep.CONTACT_DETAILS];
}
