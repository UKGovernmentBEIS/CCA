import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionWithDateComponent,
  DecisionWithDateFormModel,
  facilityDecisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toFacilitySummaryDataWithStatus,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationFacilityReviewDecision } from 'cca-api';

import { resetDetermination } from 'src/app/requests/tasks/underlying-agreement-review/utils';

import { createSaveFacilityDecisionActionDTO } from '../../../transform';

@Component({
  selector: 'cca-facility-decision',
  templateUrl: './facility-decision.component.html',
  standalone: true,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReactiveFormsModule,
    DecisionWithDateComponent,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  providers: [facilityDecisionFormProvider()],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility().status === 'NEW'
        ? this.facility()
        : this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
      this.facility().status === 'NEW'
        ? this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)()
        : this.requestTaskStore.select(
            underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
          )(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility(),
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  submit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const facility = this.facility();
    const formValue = this.form.value;

    // If the facility is new and the decision type is ACCEPTED, we need to set the change start date.
    let changeStartDate: boolean;
    if (this.facility().status === 'NEW' && formValue.type === 'ACCEPTED') changeStartDate = !!formValue.changeDate[0];

    // Create the decision object
    const decision: UnderlyingAgreementVariationFacilityReviewDecision = {
      type: formValue.type,
      changeStartDate,
      startDate: formValue.startDate as any, // bypass incorrect api type. Should be date, it is string
      details: {
        notes: formValue.notes,
        files: formValue.files.map((f) => f.uuid),
      },
      facilityStatus: facility.status,
    };

    // Update review sections completed
    const reviewSectionsCompleted = produce(payload.reviewSectionsCompleted, (draft) => {
      draft[facility.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    // Clear determination type
    const determination = resetDetermination(payload.determination);

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveFacilityDecisionActionDTO(
      requestTaskId,
      facility.facilityId,
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
