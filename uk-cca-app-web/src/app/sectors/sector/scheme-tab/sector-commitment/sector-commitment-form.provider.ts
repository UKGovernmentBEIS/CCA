import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import BigNumber from 'bignumber.js';

import { TargetCommitmentDTO } from 'cca-api';

import { getCca3SchemeFromRoute, sortTargetCommitments } from '../scheme-tab.utils';

const TARGET_COMMITMENT_ERROR_MESSAGE = 'Enter a numerical value, between - 100 and 100 with up to 3 decimal places';

export type SectorCommitmentFormModel = FormGroup<{
  commitments: FormArray<FormControl<TargetCommitmentDTO['targetImprovement']>>;
}>;

export const SECTOR_COMMITMENT_FORM = new InjectionToken<SectorCommitmentFormModel>('Sector commitment form');

const toPercentInputValue = (targetImprovement: TargetCommitmentDTO['targetImprovement']): string =>
  targetImprovement ? new BigNumber(targetImprovement).multipliedBy(100).toString() : '0';

export const SectorCommitmentFormProvider: Provider = {
  provide: SECTOR_COMMITMENT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const cca3Scheme = getCca3SchemeFromRoute(route);
    const commitments = sortTargetCommitments(cca3Scheme?.targetSet?.targetCommitments);

    return fb.group({
      commitments: fb.array(
        commitments.map((commitment) => {
          return fb.control(toPercentInputValue(commitment.targetImprovement), [
            GovukValidators.required('Enter a target commitment'),
            GovukValidators.numberInExclusiveRangeWithMaxDecimals(-100, 100, 3, TARGET_COMMITMENT_ERROR_MESSAGE),
          ]);
        }),
      ),
    });
  },
};
