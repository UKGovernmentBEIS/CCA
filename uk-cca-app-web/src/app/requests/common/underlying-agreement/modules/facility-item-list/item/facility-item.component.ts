import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Observable, switchMap } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextInputComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { FacilityService } from 'cca-api';

import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep } from '../../../underlying-agreement.types';
import { ADD_FACILITY_FORM, FacilityItemFormModel, FacilityItemFormProvider } from './facility-item-form.provider';

@Component({
  selector: 'cca-facility-item',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, TextInputComponent, RouterLink],
  templateUrl: './facility-item.component.html',
  providers: [FacilityItemFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly facilityService = inject(FacilityService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityItemFormModel>>(ADD_FACILITY_FORM);

  protected readonly facilityId = this.form.value.facilityId;

  private readonly requestInfo = this.requestTaskStore.select(requestTaskQuery.selectRequestInfo);

  onSubmit() {
    if (this.facilityId) {
      this.save(this.facilityId).subscribe();
    } else {
      this.facilityService
        .generateFacilityId(this.requestInfo().accountId)
        .pipe(switchMap((facility) => this.save(facility.facilityId)))
        .subscribe();
    }
  }

  private save(facilityId: string): Observable<string> {
    return this.taskService.saveSubtask(
      MANAGE_FACILITIES_SUBTASK,
      this.facilityId ? ManageFacilitiesWizardStep.EDIT_FACILITY : ManageFacilitiesWizardStep.ADD_FACILITY,
      this.activatedRoute,
      { name: this.form.value.name, facilityId, status: this.form.value.status ?? 'NEW' },
    );
  }
}
