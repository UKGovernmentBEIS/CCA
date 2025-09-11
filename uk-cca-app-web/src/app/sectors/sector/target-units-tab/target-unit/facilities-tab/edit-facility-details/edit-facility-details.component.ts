import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError } from 'rxjs';

import {
  ConditionalContentDirective,
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { FacilityInfoDTO, UpdateFacilityDataService } from 'cca-api';

import {
  EDIT_FACILITY_DETAILS_FORM_PROVIDER,
  EditFacilityDetailsFormModel,
  editFacilityDetailsFormProvider,
} from './edit-facility-details-form.provider';

@Component({
  selector: 'cca-edit-facility-details',
  templateUrl: './edit-facility-details.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    RouterLink,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    DateInputComponent,
    ConditionalContentDirective,
  ],
  providers: [editFacilityDetailsFormProvider()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EditFacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly updateFacilityDataService = inject(UpdateFacilityDataService);

  protected readonly facilityDetails = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;
  protected readonly today = new Date();

  protected readonly form = inject<EditFacilityDetailsFormModel>(EDIT_FACILITY_DETAILS_FORM_PROVIDER);

  onSubmit() {
    this.updateFacilityDataService
      .updateFacilitySchemeExitDate(this.facilityDetails.facilityId, {
        schemeExitDate: this.form.value?.schemeExitDate?.toISOString() ?? null,
      })
      .pipe(
        catchError((err) => {
          throw new Error(err);
        }),
      )
      .subscribe(() => this.router.navigate(['..', 'details'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
