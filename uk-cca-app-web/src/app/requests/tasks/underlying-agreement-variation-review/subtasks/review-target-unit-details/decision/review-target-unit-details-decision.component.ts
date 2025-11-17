import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, of, take } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CompaniesHouseDetailsComponent,
  CompaniesHouseState,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  decisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toReviewTargetUnitDetailsUNAReviewSummaryData,
  transformAccountReferenceData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { produce } from 'immer';

import { CompaniesInformationService, CompanyProfileDTO } from 'cca-api';

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  templateUrl: './review-target-unit-details-decision.component.html',
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    DecisionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
    CompaniesHouseDetailsComponent,
  ],
  providers: [decisionFormProvider('TARGET_UNIT_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsDecisionComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly companiesInformationService = inject(CompaniesInformationService);

  protected readonly toggleCompaniesHouseDetailsCtrl = new FormControl<boolean>(false);
  protected readonly toggleCompaniesHouseDetails = toSignal(this.toggleCompaniesHouseDetailsCtrl.valueChanges, {
    initialValue: false,
  });

  protected readonly mainColumnClass = computed(() =>
    this.toggleCompaniesHouseDetails() ? 'govuk-grid-column-two-thirds' : 'govuk-grid-column-full',
  );

  private readonly companiesHouseDetailsResponse = signal<CompanyProfileDTO | null>(null);

  protected readonly companiesHouseState = computed<CompaniesHouseState>(() => {
    const response = this.companiesHouseDetailsResponse();
    return {
      details: typeof response === 'object' ? response : null,
      error: typeof response === 'number' ? response : null,
      address: typeof response === 'object' ? transformAddress(response?.address).join('\n') : null,
    };
  });

  protected readonly form = inject(DECISION_FORM_PROVIDER);

  private readonly accountReferenceData = this.store.select(underlyingAgreementQuery.selectAccountReferenceData);
  private readonly originalTargetUnitDetails = computed(() =>
    transformAccountReferenceData(this.accountReferenceData()),
  );

  private readonly targetUnitDetails = this.store.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly tuDetails = computed(() =>
    this.targetUnitDetails() ? this.targetUnitDetails() : this.originalTargetUnitDetails(),
  );

  protected readonly summaryDataOriginal = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.originalTargetUnitDetails(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.targetUnitDetails(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
  );

  ngOnInit() {
    if (this.tuDetails().companyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.tuDetails().companyRegistrationNumber)
        .pipe(
          take(1),
          catchError((err) => of(err.status)),
        )
        .subscribe((res) => {
          if (typeof res === 'object') {
            this.companiesHouseDetailsResponse.set(res);
          }
        });
    }
  }

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const decision = {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files?.map((f: any) => f.uuid) || [],
      },
    };

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_UNIT_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
