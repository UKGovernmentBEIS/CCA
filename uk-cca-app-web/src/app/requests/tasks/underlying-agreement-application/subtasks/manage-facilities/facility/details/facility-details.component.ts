import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  applySchemeVersionsSideEffect,
  FacilityDetailsFormComponent,
  FacilityDetailsFormModel,
  FacilityDetailsFormProvider,
  FacilityWizardStep,
  isCCA2FacilityWizardCompleted,
  isCCA3FacilityWizardCompleted,
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { produce } from 'immer';

import { Facility, UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-facility-details',
  template: `<cca-facility-details-form (submitChange)="onSubmit($event)" />`,
  imports: [FacilityDetailsFormComponent],
  providers: [FacilityDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  onSubmit(form: FormGroup<FacilityDetailsFormModel>) {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);

    let updatedPayload = updateFacilityDetails(actionPayload, form, this.facilityId);

    updatedPayload = applySchemeVersionsSideEffect(
      updatedPayload,
      this.facilityId,
    ) as UnderlyingAgreementApplySavePayload;

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementSubmitRequestTaskPayload) => {
        const facility = payload.underlyingAgreement.facilities.find((f) => f.facilityId === this.facilityId);
        this.router.navigate(nextRoute(facility), { relativeTo: this.activatedRoute });
      });
  }
}

function updateFacilityDetails(
  payload: UnderlyingAgreementApplySavePayload,
  form: FormGroup<FacilityDetailsFormModel>,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const participatingSchemeVersions = form.value.participatingSchemeVersions;

    draft.facilities[facilityIndex].facilityDetails = {
      ...draft.facilities[facilityIndex].facilityDetails,
      name: form.controls.name.value,
      participatingSchemeVersions,
      isCoveredByUkets: form.value.isCoveredByUkets,
      uketsId: form.value.uketsId,
      applicationReason: form.getRawValue().applicationReason,
      previousFacilityId: form.getRawValue().previousFacilityId,
      facilityAddress: form.getRawValue().facilityAddress,
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
