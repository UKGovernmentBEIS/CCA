import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  FacilityItemListComponent,
  MANAGE_FACILITIES_SUBTASK,
  TaskItemStatus,
  underlyingAgreementQuery,
} from '@requests/common';

@Component({
  selector: 'cca-una-manage-facilities-summary',
  standalone: true,
  imports: [
    PageHeadingComponent,
    RouterLink,
    ButtonDirective,
    PendingButtonDirective,
    FacilityItemListComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './manage-facilities-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesSummaryComponent {
  private readonly taskService = inject(TaskService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly manageFacilities = this.requestTaskStore.select(underlyingAgreementQuery.selectManageFacilities);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly isCompleted: Signal<boolean> = computed(
    () =>
      this.requestTaskStore.select(underlyingAgreementQuery.selectStatusForSubtask(MANAGE_FACILITIES_SUBTASK))() ===
      TaskItemStatus.COMPLETED,
  );

  onSubmit() {
    this.taskService
      .submitSubtask(MANAGE_FACILITIES_SUBTASK)
      .subscribe(() => this.router.navigate(['../../../'], { relativeTo: this.activatedRoute }));
  }
}
