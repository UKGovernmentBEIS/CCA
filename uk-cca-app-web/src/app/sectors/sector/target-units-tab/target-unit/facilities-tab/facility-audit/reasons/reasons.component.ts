import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CheckboxComponent, CheckboxesComponent, GovukValidators, TextareaComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';
import { FacilityAuditReasonPipe } from '@shared/pipes';

import { FacilityAuditUpdateDTO, FacilityInfoDTO } from 'cca-api';

import { FacilityAuditStore } from '../facility-audit.store';

@Component({
  selector: 'cca-reasons',
  templateUrl: './reasons.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    WizardStepComponent,
    CheckboxComponent,
    CheckboxesComponent,
    TextareaComponent,
    FacilityAuditReasonPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly facilityAuditStore = inject(FacilityAuditStore);

  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;

  protected readonly reasonOptions: FacilityAuditUpdateDTO['reasons'] = [
    'ELIGIBILITY',
    'SEVENTY_RULE_EVALUATION',
    'BASE_YEAR_DATA',
    'REPORTING_DATA',
    'NON_COMPLIANCE',
    'OTHER',
  ];

  protected readonly form = new FormGroup({
    reasons: new FormControl(this.facilityAuditStore.state.reasons, {
      validators:
        this.facilityAuditStore.state.reasons.length === 0
          ? [GovukValidators.required('Select at least one reason for the audit')]
          : [],
    }),
    comments: new FormControl(this.facilityAuditStore.state.comments, {
      validators:
        this.facilityAuditStore.state.comments.length === 0 ? [GovukValidators.required('Enter a comment')] : [],
    }),
  });

  onSubmit() {
    const state: FacilityAuditUpdateDTO = {
      ...this.facilityAuditStore.state,
      reasons: this.form.value.reasons,
      comments: this.form.value.comments,
    };

    this.facilityAuditStore
      .updateAudit(state, this.facilityInfoDTO.facilityId)
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.activatedRoute, fragment: 'audit' }));
  }
}
