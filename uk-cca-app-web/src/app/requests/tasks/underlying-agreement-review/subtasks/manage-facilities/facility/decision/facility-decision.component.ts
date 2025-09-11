import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionWithDateComponent,
  DecisionWithDateFormModel,
  facilityDecisionFormProvider,
  isCCA3Scheme,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toFacilityWizardSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementFacilityReviewDecision } from 'cca-api';

import { createSaveFacilityDecisionActionDTO } from '../../../../transform';
import { resetDetermination } from '../../../../utils';

@Component({
  selector: 'cca-facility-decision',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
        <cca-summary [data]="summaryData()" />
        <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
          <cca-decision-with-date />
        </cca-wizard-step>
      </div>
    }

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../..">Return to: Manage facilities</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ReactiveFormsModule,
    DecisionWithDateComponent,
    WizardStepComponent,
    RouterLink,
  ],
  providers: [facilityDecisionFormProvider()],
  standalone: true,
})
export class FacilityDecisionComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionWithDateFormModel>(DECISION_FORM_PROVIDER);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId));

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly participatingSchemeVersions = computed(
    () =>
      this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))()?.facilityDetails
        ?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.store.select(underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()))(),
  );

  protected readonly summaryData = computed(() =>
    toFacilityWizardSummaryData(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
      this.store.select(underlyingAgreementQuery.selectAttachments)(),
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const changeStartDate = this.form.value?.changeDate?.[0];

    const decision: UnderlyingAgreementFacilityReviewDecision = {
      type: this.form.value.type,
      changeStartDate: this.form.value.type === 'ACCEPTED' ? !!changeStartDate : null,
      startDate:
        this.form.value.startDate instanceof Date
          ? this.form.value.startDate.toISOString().split('T')[0]
          : this.form.value.startDate,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files.map((f) => f.uuid),
      },
    };

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const payload = createSaveFacilityDecisionActionDTO(
      requestTaskId,
      this.facilityId,
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
