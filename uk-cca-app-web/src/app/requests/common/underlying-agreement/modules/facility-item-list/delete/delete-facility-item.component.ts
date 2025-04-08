import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';

import { underlyingAgreementQuery } from '../../../+state';
import {
  MANAGE_FACILITIES_SUBTASK,
  ManageFacilitiesWizardStep,
  toFacilityItemViewModel,
} from '../../../underlying-agreement.types';

@Component({
  selector: 'cca-delete-facility-item',
  standalone: true,
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, PendingButtonDirective, WarningTextComponent],
  templateUrl: './delete-facility-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteFacilityItemComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  protected readonly facilityName = this.facility().facilityDetails.name;

  onDelete() {
    this.taskService
      .saveSubtask(
        MANAGE_FACILITIES_SUBTASK,
        ManageFacilitiesWizardStep.DELETE_FACILITY,
        this.activatedRoute,
        toFacilityItemViewModel(this.facility()),
      )
      .subscribe();
  }
}
