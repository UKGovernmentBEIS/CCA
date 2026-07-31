import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { TargetPeriodBuyOutDetailsDTO } from 'cca-api';

export type ChangeCostFormModel = FormGroup<{
  buyOutCost: FormControl<number | null>;
}>;

export const CHANGE_COST_FORM = new InjectionToken<ChangeCostFormModel>('Change cost form');

export const ChangeCostFormProvider: Provider = {
  provide: CHANGE_COST_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const targetPeriodDetails = route.snapshot.data.targetPeriodDetails as TargetPeriodBuyOutDetailsDTO;

    return fb.group({
      buyOutCost: fb.control(targetPeriodDetails?.buyOutCost ?? null, [
        GovukValidators.required('Enter a buy-out cost'),
        GovukValidators.naturalNumber('Enter a positive integer value'),
      ]),
    });
  },
};
