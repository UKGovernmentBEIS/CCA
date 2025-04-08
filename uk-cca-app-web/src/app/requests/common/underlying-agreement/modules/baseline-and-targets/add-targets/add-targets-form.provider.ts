import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
      GovukValidators.required('Enter a numerical value, without alpha or special characters'),
      GovukValidators.max(100, 'Enter a number less than 100'),
      GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
    ]);

    improvementControl.valueChanges.pipe(takeUntilDestroyed()).subscribe((value) => {
      const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(isTargetPeriodFive))();

      const targetComposition = requestTaskStore.select(
        underlyingAgreementQuery.selectTargetComposition(isTargetPeriodFive),
      )();

      const energyOrCarbon = baselineData?.energy;
      const agreementCompositionType = targetComposition.agreementCompositionType;

      // Novem agreement composition has no target value
      if (agreementCompositionType === 'NOVEM') return;

      // calculate target value depending on agreement composition
      const targets =
        agreementCompositionType === 'ABSOLUTE'
          ? calculateAbsoluteTarget(energyOrCarbon, value, isTargetPeriodFive)
          : calculateRelativeTarget(energyOrCarbon, baselineData.throughput, value);

      if (typeof targets === 'number') targetsControl.patchValue(targets);
    });

    return fb.group({
      improvement: improvementControl,
      target: targetsControl,
    });
  },
};
