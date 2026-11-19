import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  ConditionalContentDirective,
  DateInputComponent,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
} from '@netz/govuk-components';
import { DurationPipe } from '@shared/pipes';

import { FacilityCertificationStatusDTO, FacilityInfoDTO, UpdateFacilityDataService } from 'cca-api';

import {
  CHANGE_CERTIFICATION_STATUS_FORM,
  ChangeCertificationStatusFormProvider,
} from './change-certification-status-form.provider';

@Component({
  selector: 'cca-change-certification-status',
  templateUrl: './change-certification-status.component.html',
  providers: [ChangeCertificationStatusFormProvider],
  imports: [
    PageHeadingComponent,
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
    DurationPipe,
    RadioComponent,
    RadioOptionComponent,
    RouterLink,
    ErrorSummaryComponent,
    ConditionalContentDirective,
    DateInputComponent,
  ],
})
export class ChangeCertificationStatusComponent {
  private readonly updateFacilityDataService = inject(UpdateFacilityDataService);
  private readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = +this.activatedRoute.snapshot.paramMap.get('facilityId');

  protected readonly facilityBusinessId = (this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO)
    .facilityBusinessId;
  protected readonly certificationDetails = this.activatedRoute.snapshot.data
    .certificationDetails as FacilityCertificationStatusDTO;

  // 'CP8' -> '8'
  protected readonly certificationPeriodDuration = `Certification period ${this.certificationDetails.certificationPeriod.replace('CP', '')} duration`;

  protected readonly hint = `This option will determine whether this facility will be certified until the end of ${this.certificationDetails.certificationPeriod} or not.`;

  readonly form = inject(CHANGE_CERTIFICATION_STATUS_FORM);

  protected readonly hasFormErrors = signal(false);

  onSubmit() {
    if (this.form.invalid) {
      this.hasFormErrors.set(true);
      return;
    }

    const { certificationStatus, startDate } = this.form.getRawValue();

    this.updateFacilityDataService
      .updateFacilityCertificationStatus(this.facilityId, {
        certificationStatus,
        certificationPeriodId: this.certificationDetails.certificationPeriodId,
        startDate: certificationStatus === 'CERTIFIED' ? startDate.toISOString().slice(0, 10) : null,
      })
      .subscribe(() =>
        this.router.navigate(['../..'], { relativeTo: this.activatedRoute, fragment: 'certification-status' }),
      );
  }
}
