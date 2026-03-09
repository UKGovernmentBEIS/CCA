import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { MiReportUserDefinedDTO } from 'cca-api';

export type MiReportFormModel = FormGroup<{
  reportName: FormControl<string>;
  description: FormControl<string>;
  queryDefinition: FormControl<string>;
}>;

export const MI_REPORT_FORM = new InjectionToken<MiReportFormModel>('MI report form');

export const MiReportFormProvider: Provider = {
  provide: MI_REPORT_FORM,
  useFactory: () => {
    const fb = inject(FormBuilder);
    const route = inject(ActivatedRoute);
    const query = route.snapshot.data['query'] as MiReportUserDefinedDTO | undefined;

    return fb.group({
      reportName: fb.control<string>(query?.reportName ?? null, [GovukValidators.required('Enter a report name')]),
      description: fb.control<string>(query?.description ?? null),
      queryDefinition: fb.control<string>(query?.queryDefinition ?? null, [GovukValidators.required('Enter a query')]),
    });
  },
};
