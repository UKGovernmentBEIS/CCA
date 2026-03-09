import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementVariationRegulatorLedQuery } from '@requests/common';

export type ChargeDateFormModel = {
  hasChargeStartDate: FormControl<boolean>;
  chargeStartDate: FormControl<Date>;
};

export const CHARGE_DATE_FORM = new InjectionToken<ChargeDateFormModel>('Charge date form');

export const ChargeDateFormProvider: Provider = {
  provide: CHARGE_DATE_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore],
  useFactory: (fb: FormBuilder, route: ActivatedRoute, store: RequestTaskStore) => {
    const facilityId = route.snapshot.params?.facilityId;

    const chargeStartDate = store.select(
      underlyingAgreementVariationRegulatorLedQuery.selectFacilityChargeStartDate(facilityId),
    )();

    const group = fb.group<ChargeDateFormModel>({
      hasChargeStartDate: fb.control(true),
      chargeStartDate: fb.control(chargeStartDate ? new Date(chargeStartDate) : null, {
        validators: [GovukValidators.required('Select a date')],
      }),
    });

    group.controls.hasChargeStartDate.valueChanges.pipe(takeUntilDestroyed()).subscribe((hasDate) => {
      if (hasDate) {
        group.controls.chargeStartDate.enable();
      } else {
        group.controls.chargeStartDate.reset();
        group.controls.chargeStartDate.disable();
      }
    });

    return group;
  },
};
