import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
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

import { FacilityInfoDTO, UpdateFacilityDataService } from 'cca-api';

import {
  CHANGE_CERTIFICATION_STATUS_FORM,
  ChangeCertificationStatusFormModel,
  ChangeCertificationStatusFormProvider,
} from './change-certification-status-form.provider';

@Component({
  selector: 'cca-change-certification-status',
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
  templateUrl: './change-certification-status.component.html',
  providers: [ChangeCertificationStatusFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeCertificationStatusComponent {
  private readonly updateFacilityDataService = inject(UpdateFacilityDataService);
  private readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly facilityId = +this.activatedRoute.snapshot.paramMap.get('facilityId');
  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;

  readonly form = inject<ChangeCertificationStatusFormModel>(CHANGE_CERTIFICATION_STATUS_FORM);

  protected readonly hasFormErrors = signal(false);

  protected readonly certificationDetails = this.facilityInfoDTO.facilityCertificationDetails.find(
    (entry) => entry.certificationPeriod === this.activatedRoute.snapshot.paramMap.get('certificationPeriod'),
  );

  readonly certificationPeriodId = this.certificationDetails.certificationPeriod === 'CP6' ? 1 : 2;

  readonly certificationPeriodDuration =
    this.certificationDetails.certificationPeriod === 'CP6'
      ? 'Certification Period 6 duration'
      : 'Certification Period 7 duration';

  protected readonly hint = `This option will determine whether this facility will be certified until the end of
  ${this.certificationDetails.certificationPeriod === 'CP6' ? 'CP6' : 'CP7'} or not.`;

  onSubmit() {
    if (this.form.invalid) {
      this.hasFormErrors.set(true);
      return;
    }

    this.updateFacilityDataService
      .updateFacilityCertificationStatus(this.facilityId, {
        certificationStatus: this.form.value.certificationStatus,
        certificationPeriodId: this.certificationPeriodId,
        startDate: this.form.value.certificationStatus === 'CERTIFIED' ? this.form.value.startDate : null,
      })
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.activatedRoute }));
  }
}
