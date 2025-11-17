import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery } from '@requests/common';
import { RequestTaskFileService } from '@shared/services';
import { Improvement } from '@shared/types';

export type FacilityTargetsFormModel = FormGroup<{
  tp7: FormControl<number>;
  tp8: FormControl<number>;
  tp9: FormControl<number>;
}>;

export const FACILITY_TARGETS_FORM = new InjectionToken<FacilityTargetsFormModel>('Facility targets form');

export const FacilityTargetsFormProvider: Provider = {
  provide: FACILITY_TARGETS_FORM,
  deps: [ActivatedRoute, FormBuilder, RequestTaskStore, RequestTaskFileService],
  useFactory: (activatedRoute: ActivatedRoute, fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const facilityId = activatedRoute.snapshot.params.facilityId;
    const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
    const facilityIndex = una.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;

    const improvements = requestTaskStore.select(underlyingAgreementQuery.selectFacilityTargets(facilityIndex))()
      ?.improvements;

    return fb.group({
      tp7: fb.control(improvements?.[Improvement.TP7] ?? null, {
        validators: [
          GovukValidators.required('Enter a numerical value, without alpha or special characters'),
          GovukValidators.max(100, 'Enter a number less than 100'),
          GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
        ],
      }),
      tp8: fb.control(improvements?.[Improvement.TP8] ?? null, {
        validators: [
          GovukValidators.required('Enter a numerical value, without alpha or special characters'),
          GovukValidators.max(100, 'Enter a number less than 100'),
          GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
        ],
      }),
      tp9: fb.control(improvements?.[Improvement.TP9] ?? null, {
        validators: [
          GovukValidators.required('Enter a numerical value, without alpha or special characters'),
          GovukValidators.max(100, 'Enter a number less than 100'),
          GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
        ],
      }),
    });
  },
};
