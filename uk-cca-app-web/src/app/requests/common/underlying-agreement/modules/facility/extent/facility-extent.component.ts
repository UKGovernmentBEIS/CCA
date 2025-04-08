import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { transformFilesToUUIDsList } from '@shared/utils';

import { FacilityExtent } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';
import { FACILITIES_SUBTASK, FacilityWizardStep } from '../../../underlying-agreement.types';
import {
  FACILITY_EXTENT_FORM,
  FacilityExtentFormModel,
  FacilityExtentFormProvider,
} from './facility-extent-form.provider';

@Component({
  selector: 'cca-facility-extent',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioComponent,
    RadioOptionComponent,
    FileInputComponent,
    DetailsComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './facility-extent.component.html',
  providers: [FacilityExtentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityExtentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<FacilityExtentFormModel>>(FACILITY_EXTENT_FORM);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  private readonly activitiesClaimedExists: Signal<FacilityExtent['areActivitiesClaimed']> = toSignal(
    this.form.get('areActivitiesClaimed').valueChanges,
    {
      initialValue: this.form.get('areActivitiesClaimed').value,
    },
  );

  protected readonly isActivitiesDescriptionFileExist: Signal<boolean> = computed(() => {
    if (this.activitiesClaimedExists()) {
      this.form.get('activitiesDescriptionFile').enable();
      return true;
    } else {
      this.form.get('activitiesDescriptionFile').disable();
      this.form.get('activitiesDescriptionFile').reset();
      return false;
    }
  });

  getDownloadUrl(uuid: string) {
    return ['../../../../file-download', uuid];
  }

  onSubmit() {
    const facility = {
      facilityId: this.facilityId,
      facilityExtent: {
        ...this.form.value,
        manufacturingProcessFile: transformFilesToUUIDsList(this.form.value.manufacturingProcessFile),
        processFlowFile: transformFilesToUUIDsList(this.form.value.processFlowFile),
        annotatedSitePlansFile: transformFilesToUUIDsList(this.form.value.annotatedSitePlansFile),
        eligibleProcessFile: transformFilesToUUIDsList(this.form.value.eligibleProcessFile),
        activitiesDescriptionFile: transformFilesToUUIDsList(this.form.value?.activitiesDescriptionFile),
      },
    };
    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.EXTENT, this.activatedRoute, facility)
      .subscribe();
  }
}
