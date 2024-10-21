import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  FACILITIES_SUBTASK,
  toFacilitySummaryDataWithStatusAndDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-facility-check-answers',
  templateUrl: './facility-check-answers.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilityCheckAnswersComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );
  protected readonly facility = computed(() =>
    this.store
      .select(underlyingAgreementQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );

  protected readonly summaryData = computed(() =>
    toFacilitySummaryDataWithStatusAndDecision(
      this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.decision,
      {
        submit: this.store.select(underlyingAgreementQuery.selectAttachments)(),
        review: this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    const payload = this.store.select(underlyingAgreementQuery.selectPayload)();
    this.store.setPayload({ ...payload, currentFacilityId: this.facilityId });

    this.taskService
      .submitSubtask(FACILITIES_SUBTASK)
      .subscribe(() => this.router.navigate(['../../../..'], { relativeTo: this.activatedRoute }));
  }
}
