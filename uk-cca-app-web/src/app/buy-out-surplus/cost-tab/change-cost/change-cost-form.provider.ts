import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { TargetPeriodBuyOutDetailsDTO } from 'cca-api';

const BUY_OUT_COST_ERROR_MESSAGE = 'Enter a number greater than 0 with up to 2 decimal places';

export type ChangeCostFormModel = FormGroup<{
  buyOutCost: FormControl<string | null>;
}>;

export const CHANGE_COST_FORM = new InjectionToken<ChangeCostFormModel>('Change cost form');

export const ChangeCostFormProvider: Provider = {
  provide: CHANGE_COST_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const targetPeriodDetails = route.snapshot.data.targetPeriodDetails as TargetPeriodBuyOutDetailsDTO;
    const buyOutCost = targetPeriodDetails?.buyOutCost;

    return fb.group({
      buyOutCost: fb.control<string | null>(buyOutCost ?? null, [
        GovukValidators.required('Enter a buy-out cost'),
        GovukValidators.builder(BUY_OUT_COST_ERROR_MESSAGE, GovukValidators.maxIntegerAndDecimalsValidator(18, 2)),
        GovukValidators.positiveNumber(BUY_OUT_COST_ERROR_MESSAGE),
      ]),
    });
  },
};
