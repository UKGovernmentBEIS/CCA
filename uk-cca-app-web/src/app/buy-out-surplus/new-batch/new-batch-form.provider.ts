import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { TargetPeriodType } from '@shared/types';

import { AvailableTargetPeriodsBuyOutDTO } from 'cca-api';

import { sortTargetPeriodsDesc } from './utils';

export type NewBatchFormModel = FormGroup<{
  targetPeriodType: FormControl<TargetPeriodType | null>;
}>;

export const NEW_BATCH_FORM = new InjectionToken<NewBatchFormModel>('New batch form');

export const NewBatchFormProvider: Provider = {
  provide: NEW_BATCH_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const availableTargetPeriods = route.snapshot.data.availableTargetPeriods as AvailableTargetPeriodsBuyOutDTO;

    // the latest available target period is the one the batch defaults to, e.g. TP7 over TP6 during an overlap
    const [latestTargetPeriod] = sortTargetPeriodsDesc(availableTargetPeriods?.currentTargetPeriods);

    return fb.group({
      targetPeriodType: fb.control(latestTargetPeriod?.businessId ?? null, [
        GovukValidators.required('Select a target period'),
      ]),
    });
  },
};
