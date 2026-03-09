import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { TextInputComponent, WizardStepComponent } from '@shared/components';

import { underlyingAgreementQuery } from '../../+state';
import { FacilityTargetsSubmitEvent } from '../types';
import { FACILITY_TARGETS_FORM, FacilityTargetsFormModel, FacilityTargetsFormProvider } from './targets-form.provider';

@Component({
  selector: 'cca-common-facility-targets',
  templateUrl: './targets.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, TextInputComponent, RouterLink],
  providers: [FacilityTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommonFacilityTargetsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);

  protected readonly form = inject<FacilityTargetsFormModel>(FACILITY_TARGETS_FORM);

  protected readonly submitted = output<FacilityTargetsSubmitEvent>();

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onSubmit() {
    this.submitted.emit({ form: this.form, facilityId: this.facilityId });
  }
}
