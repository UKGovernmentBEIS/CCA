import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, ErrorSummaryComponent, LinkDirective } from '@netz/govuk-components';
import {
  atLeastOneActiveValidator,
  FacilityItemListComponent,
  FacilityItemViewModel,
  MANAGE_FACILITIES_SUBTASK,
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { UnderlyingAgreementContainer } from 'cca-api';

@Component({
  selector: 'cca-una-manage-facilities-summary',
  standalone: true,
  imports: [
    LinkDirective,
    PageHeadingComponent,
    RouterLink,
    ButtonDirective,
    PendingButtonDirective,
    FacilityItemListComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  templateUrl: './manage-facilities-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesSummaryComponent {
  private readonly taskService = inject(TaskService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  private readonly originalData: Signal<UnderlyingAgreementContainer> = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementContainer,
  );

  protected readonly originalFacilityItems: Signal<FacilityItemViewModel[]> = computed(() => {
    return this.originalData().underlyingAgreement.facilities.map((of) => ({
      name: of.facilityDetails.name,
      facilityId: of.facilityId,
      excludedDate: of.excludedDate,
      status: of.status,
    }));
  });

  protected readonly manageFacilities = this.requestTaskStore.select(underlyingAgreementQuery.selectManageFacilities);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  protected readonly isCompleted: Signal<boolean> = computed(
    () =>
      this.requestTaskStore.select(underlyingAgreementQuery.selectStatusForSubtask(MANAGE_FACILITIES_SUBTASK))() ===
      TaskItemStatus.COMPLETED,
  );

  protected readonly form = this.fb.group({
    list: [this.manageFacilities().facilityItems, { validators: [atLeastOneActiveValidator()] }],
  });

  protected readonly showErrorSummary = signal(false);

  onSubmit() {
    if (this.form.valid) {
      this.showErrorSummary.set(false);

      this.taskService
        .submitSubtask(MANAGE_FACILITIES_SUBTASK)
        .subscribe(() => this.router.navigate(['../../../'], { relativeTo: this.activatedRoute }));
    } else {
      this.showErrorSummary.set(true);
    }
  }
}
