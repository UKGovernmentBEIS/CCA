import { InjectionToken, Provider } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

export type EtsReportingForm = FormGroup<{ targetYear: FormControl<number | null> }>;

export const ETS_REPORTING_FORM = new InjectionToken<EtsReportingForm>('ETS reporting form');

export const etsReportingFormProvider: Provider = {
  provide: ETS_REPORTING_FORM,
  useFactory: (): EtsReportingForm =>
    new FormGroup({
      targetYear: new FormControl<number | null>(null, [GovukValidators.required('Select a year')]),
    }),
};
