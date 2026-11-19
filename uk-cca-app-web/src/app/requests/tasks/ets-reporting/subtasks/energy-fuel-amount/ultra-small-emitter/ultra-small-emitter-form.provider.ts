import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { etsReportingQuery } from '../../../ets-reporting.selectors';

export type UltraSmallEmitterForm = FormGroup<{
  isUltraSmallEmitter: FormControl<boolean | null>;
  provideFuelDetails: FormControl<boolean | null>;
}>;

export const ULTRA_SMALL_EMITTER_FORM = new InjectionToken<UltraSmallEmitterForm>('ETS ultra-small emitter form');

export const UltraSmallEmitterFormProvider: Provider = {
  provide: ULTRA_SMALL_EMITTER_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore): UltraSmallEmitterForm => {
    const input = store.select(etsReportingQuery.selectInputDetails)();
    const form = fb.group({
      isUltraSmallEmitter: fb.control<boolean | null>(input?.isUltraSmallEmitter ?? null, [
        GovukValidators.required('Select yes if this facility is registered as an ultra-small emitter (USE) in UK ETS'),
      ]),
      provideFuelDetails: fb.control<boolean | null>(input?.provideFuelDetails ?? null),
    });
    const updateVoluntaryQuestion = (isUltraSmallEmitter: boolean | null) => {
      const control = form.controls.provideFuelDetails;
      if (isUltraSmallEmitter === true) {
        control.setValidators(GovukValidators.required('Select yes if you want to voluntarily provide fuel details'));
      } else {
        control.clearValidators();
        control.reset(null, { emitEvent: false });
      }
      control.updateValueAndValidity({ emitEvent: false });
    };
    updateVoluntaryQuestion(form.controls.isUltraSmallEmitter.value);
    form.controls.isUltraSmallEmitter.valueChanges.pipe(takeUntilDestroyed()).subscribe(updateVoluntaryQuestion);
    return form;
  },
};
