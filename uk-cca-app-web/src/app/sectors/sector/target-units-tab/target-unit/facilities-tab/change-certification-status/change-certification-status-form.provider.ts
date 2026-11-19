import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukDatePipe } from '@netz/common/pipes';
import { GovukValidators } from '@netz/govuk-components';
import { dateRangeValidator, futureDateValidator } from '@shared/validators';

import { FacilityCertificationStatusDTO } from 'cca-api';

export type ChangeCertificationStatusFormModel = FormGroup<{
  certificationStatus: FormControl<'CERTIFIED' | 'DECERTIFIED' | null>;
  startDate: FormControl<Date | null>;
}>;

export const CHANGE_CERTIFICATION_STATUS_FORM = new InjectionToken<ChangeCertificationStatusFormModel>(
  'Change certification status form',
);

export const ChangeCertificationStatusFormProvider: Provider = {
  provide: CHANGE_CERTIFICATION_STATUS_FORM,
  useFactory: (): ChangeCertificationStatusFormModel => {
    const fb = inject(FormBuilder);
    const certificationDetails = inject(ActivatedRoute).snapshot.data
      .certificationDetails as FacilityCertificationStatusDTO;
    const { status, startDate, certificationPeriodStartDate, certificationPeriodEndDate } = certificationDetails;
    const datePipe = new GovukDatePipe();

    // The start date sits in the Certified option's conditional content, which enables/disables it with the selection.
    return fb.group({
      certificationStatus: fb.control<'CERTIFIED' | 'DECERTIFIED' | null>(
        status === 'NOT_YET_DEFINED' ? null : status,
        [GovukValidators.required('Select a certification status')],
      ),
      startDate: fb.control<Date | null>(
        { value: startDate ? new Date(startDate) : null, disabled: status !== 'CERTIFIED' },
        [
          GovukValidators.required('Enter a start date'),
          futureDateValidator('The start date must be today or in the past'),
          dateRangeValidator(
            certificationPeriodStartDate,
            certificationPeriodEndDate,
            `The start date must be between ${datePipe.transform(certificationPeriodStartDate)} and ${datePipe.transform(certificationPeriodEndDate)}`,
          ),
        ],
      ),
    });
  },
};
