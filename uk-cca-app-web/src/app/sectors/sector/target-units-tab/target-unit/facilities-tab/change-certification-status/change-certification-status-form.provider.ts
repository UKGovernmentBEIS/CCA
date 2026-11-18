import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { dateRangeValidator, futureDateValidator } from '@shared/validators';

import { FacilityCertificationDetailsDTO, FacilityInfoDTO } from 'cca-api';

export type ChangeCertificationStatusFormModel = FormGroup<{
  certificationStatus: FormControl<'CERTIFIED' | 'DECERTIFIED'>;
  startDate: FormControl<string>;
}>;

export const CHANGE_CERTIFICATION_STATUS_FORM = new InjectionToken('Change certification status form');

export const ChangeCertificationStatusFormProvider: Provider = {
  provide: CHANGE_CERTIFICATION_STATUS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const facilityInfoDTO = activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;
    const certificationDetails: FacilityCertificationDetailsDTO = facilityInfoDTO.facilityCertificationDetails.find(
      (entry) => entry.certificationPeriod === activatedRoute.snapshot.paramMap.get('certificationPeriod'),
    );

    const group = fb.group({
      certificationStatus: fb.control(
        certificationDetails.status === 'NOT_YET_DEFINED' ? null : certificationDetails.status,
        [GovukValidators.required('Select a status')],
      ),
      startDate: fb.control(certificationDetails?.startDate ? new Date(certificationDetails.startDate) : null, [
        GovukValidators.required('Please enter a date'),
        futureDateValidator('The certification date cannot be a future date.'),
        dateRangeValidator(
          certificationDetails.certificationPeriodStartDate,
          certificationDetails.certificationPeriodEndDate,
          `The date must be between ${certificationDetails.certificationPeriodStartDate} and ${certificationDetails.certificationPeriodEndDate}`,
        ),
      ]),
    });

    group.controls.certificationStatus.valueChanges.pipe(takeUntilDestroyed()).subscribe((status: string) => {
      const startDateCtrl = group.controls.startDate;

      if (certificationDetails.status === 'CERTIFIED' || status === 'CERTIFIED') {
        startDateCtrl.enable();
      } else {
        startDateCtrl.disable();
        startDateCtrl.reset();
      }
    });

    return group;
  },
};
