import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { FacilityAuditUpdateDTO, FacilityInfoDTO } from 'cca-api';

import { FacilityAuditStore } from '../facility-audit.store';

@Component({
  selector: 'cca-audit',
  templateUrl: './audit.component.html',
  imports: [ReactiveFormsModule, RouterLink, WizardStepComponent, RadioComponent, RadioOptionComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly facilityAuditStore = inject(FacilityAuditStore);

  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;

  protected readonly form = new FormGroup({
    auditRequired: new FormControl(this.facilityAuditStore.state.auditRequired, {
      validators: [GovukValidators.required('Make a selection')],
    }),
  });

  onSubmit() {
    const state: FacilityAuditUpdateDTO = this.form.value.auditRequired
      ? { ...this.facilityAuditStore.state, auditRequired: this.form.value.auditRequired }
      : { auditRequired: this.form.value.auditRequired, reasons: [], comments: '' };

    this.facilityAuditStore.updateAudit(state, this.facilityInfoDTO.facilityId).subscribe((audit) => {
      const path = !audit?.auditRequired || (audit?.reasons?.length && audit?.comments?.length) ? '..' : 'reasons';
      this.router.navigate([path], { relativeTo: this.activatedRoute, fragment: 'audit' });
    });
  }
}
