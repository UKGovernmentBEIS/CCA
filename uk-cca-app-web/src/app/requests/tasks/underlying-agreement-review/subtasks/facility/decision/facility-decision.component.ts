import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionWithDateComponent,
  DecisionWithDateFormModel,
  facilityDecisionFormProvider,
  FacilityWizardReviewStep,
  toFacilitySummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementReviewTaskService } from '../../../services/underlying-agreement-review-task.service';

@Component({
  selector: 'cca-facility-decision',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading>{{ facility.facilityDetails.name }} ({{ facility.facilityId }})</netz-page-heading>

        <cca-summary [data]="summaryData()" />
        <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
          <cca-decision-with-date></cca-decision-with-date>
        </cca-wizard-step>
      </div>
    }

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReactiveFormsModule,
    DecisionWithDateComponent,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [facilityDecisionFormProvider()],
  standalone: true,
})
export class FacilityDecisionComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryData = computed(() =>
    toFacilitySummaryData(
      this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
      this.store.select(underlyingAgreementQuery.selectAttachments)(),
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  submit() {
    (this.taskService as UnderlyingAgreementReviewTaskService)
      .saveFacilityDecision(this.form.value, this.facilityId)
      .subscribe(() => {
        this.router.navigate(['../', FacilityWizardReviewStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.route,
        });
      });
  }
}
