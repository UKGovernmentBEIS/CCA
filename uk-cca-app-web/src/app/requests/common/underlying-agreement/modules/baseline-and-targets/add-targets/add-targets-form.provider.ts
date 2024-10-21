import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { Targets } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  TargetPeriod,
} from '../../../underlying-agreement.types';
import { calculateAbsoluteTarget, calculateRelativeTarget } from '../baseline-and-targets.helper';

export type AddTargetsFormModel = FormGroup<{
  improvement?: FormControl<Targets['improvement']>;
  target?: FormControl<Targets['target']>;
}>;

export const ADD_TARGETS_FORM = new InjectionToken<AddTargetsFormModel>('Add targets form');

export const addTargetsFormProvider: Provider = {
  provide: ADD_TARGETS_FORM,
  deps: [FormBuilder, RequestTaskStore, BASELINE_AND_TARGETS_SUBTASK],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, targetPeriod: TargetPeriod) => {
    const isTargetPeriodFive = targetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;

    const targets = requestTaskStore.select(underlyingAgreementQuery.selectTargets(isTargetPeriodFive))();

    const targetsControl = fb.control(targets?.target ?? null);
    const improvementControl = fb.control(targets?.improvement ?? null, [
      GovukValidators.required('Enter the Improvement % for the target period.'),
    ]);
    improvementControl.valueChanges.subscribe((value) => {
      const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(isTargetPeriodFive))();
      const targetComposition = requestTaskStore.select(
        underlyingAgreementQuery.selectTargetComposition(isTargetPeriodFive),
      )();
      const performance = baselineData.performance;
      const energyOrCarbon = baselineData?.energy;
      const agreementCompositionType = targetComposition.agreementCompositionType;
      const targets =
        agreementCompositionType === 'ABSOLUTE'
          ? calculateAbsoluteTarget(energyOrCarbon, value, isTargetPeriodFive)
          : calculateRelativeTarget(performance, value);
      if (typeof targets === 'number') targetsControl.patchValue(targets);
    });
    return fb.group({
      improvement: improvementControl,
      target: targetsControl,
    });
  },
};
