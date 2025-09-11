import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';

import { underlyingAgreementQuery } from '../../+state';
import { ApplicationReasonTypePipe } from '../../pipes';
import {
  VARIATION_FACILITY_DETAILS_FORM,
  VariationFacilityDetailsFormModel,
} from './variation-facility-details-form.provider';

@Component({
  selector: 'cca-variation-facility-details-form',
  templateUrl: './variation-facility-details-form.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    AccountAddressInputComponent,
    ApplicationReasonTypePipe,
    TextInputComponent,
    CheckboxesComponent,
    CheckboxComponent,
    WizardStepComponent,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationFacilityDetailsFormComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly submitChange = output<FormGroup<VariationFacilityDetailsFormModel>>();

  protected readonly form = inject<FormGroup<VariationFacilityDetailsFormModel>>(VARIATION_FACILITY_DETAILS_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly returnPath = this.facilityId ? '../../' : '../';

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    this.submitChange.emit(this.form);
  }
}
