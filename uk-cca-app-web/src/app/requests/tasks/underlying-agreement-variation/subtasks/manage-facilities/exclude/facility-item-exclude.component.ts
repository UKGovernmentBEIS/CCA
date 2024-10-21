import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, LinkDirective, TextInputComponent } from '@netz/govuk-components';
import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep, underlyingAgreementQuery } from '@requests/common';
import { WizardStepComponent } from '@shared/components';

import { Facility } from 'cca-api';

import {
  EXCLUDE_FACILITY_FORM,
  FacilityItemExcludeFormModel,
  FacilityItemExcludeFormProvider,
} from './facility-item-exclude-form.provider';

@Component({
  selector: 'cca-facility-item-exclude',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    TextInputComponent,
    LinkDirective,
    RouterLink,
    DateInputComponent,
  ],
  templateUrl: './facility-item-exclude.component.html',
  providers: [FacilityItemExcludeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemExcludeComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityItemExcludeFormModel>>(EXCLUDE_FACILITY_FORM);
  protected readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly facility: Signal<Facility> = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacility(this.facilityId),
  );

  onSubmit() {
    this.taskService
      .saveSubtask(MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep.EXCLUDE_FACILITY, this.activatedRoute, {
        facilityId: this.facilityId,
        ...this.form.value,
      })
      .subscribe();
  }
}
