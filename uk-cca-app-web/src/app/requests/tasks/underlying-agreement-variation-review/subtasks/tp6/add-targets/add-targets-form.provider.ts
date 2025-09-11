import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { calculateAbsoluteTarget, calculateRelativeTarget, underlyingAgreementQuery } from '@requests/common';

import { Targets } from 'cca-api';

export type AddTargetsFormModel = FormGroup<{
  improvement?: FormControl<Targets['improvement']>;
  target?: FormControl<Targets['target']>;
}>;

export const ADD_TARGETS_FORM = new InjectionToken<AddTargetsFormModel>('Add targets form');

export const AddTargetsFormProvider: Provider = {
  provide: ADD_TARGETS_FORM,
  deps: [FormBuilder, RequestTaskStore, DestroyRef],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore, destroyRef: DestroyRef) => {
    const targets = requestTaskStore.select(underlyingAgreementQuery.selectTargets(false))(); // TP6

    const targetsControl = fb.control(targets?.target ?? null);

    const improvementControl = fb.control(targets?.improvement ?? null, [
      GovukValidators.required('Enter a numerical value, without alpha or special characters'),
      GovukValidators.max(100, 'Enter a number less than 100'),
      GovukValidators.maxIntegerAndDecimalsValidator(3, 7),
    ]);

    improvementControl.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((value) => {
      const baselineData = requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(false))(); // TP6

      const targetComposition = requestTaskStore.select(underlyingAgreementQuery.selectTargetComposition(false))(); // TP6

      const energyOrCarbon = baselineData?.energy;
      const agreementCompositionType = targetComposition.agreementCompositionType;

      // Novem agreement composition has no target value
      if (agreementCompositionType === 'NOVEM') return;

      // calculate target value depending on agreement composition
      const targets =
        agreementCompositionType === 'ABSOLUTE'
          ? calculateAbsoluteTarget(energyOrCarbon, value, false)
          : calculateRelativeTarget(energyOrCarbon, baselineData.throughput, value);

      if (typeof targets === 'number') targetsControl.patchValue(targets);
    });

    return fb.group({
      improvement: improvementControl,
      target: targetsControl,
    });
  },
};
