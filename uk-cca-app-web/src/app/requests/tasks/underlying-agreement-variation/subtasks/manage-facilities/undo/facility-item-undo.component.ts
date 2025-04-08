import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep, underlyingAgreementQuery } from '@requests/common';

import { Facility } from 'cca-api';

@Component({
  selector: 'cca-facility-item-undo',
  standalone: true,
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, PendingButtonDirective],
  templateUrl: './facility-item-undo.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityItemUndoComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly facility: Signal<Facility> = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacility(this.facilityId),
  );

  onSubmit() {
    this.taskService
      .saveSubtask(MANAGE_FACILITIES_SUBTASK, ManageFacilitiesWizardStep.UNDO_FACILITY, this.activatedRoute, {
        facilityId: this.facility().facilityId,
      })
      .subscribe();
  }
}
