import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, of, take } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  CompaniesHouseDetailsComponent,
  CompaniesHouseState,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toVariationTargetUnitDetailsOriginalSummaryData,
  toVariationTargetUnitDetailsSummaryData,
  transformAccountReferenceData,
  transformAccountReferenceDataToTUDetails,
  underlyingAgreementQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { produce } from 'immer';

import {
  CompaniesInformationService,
  CompanyProfileDTO,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps, resetReviewSection } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './review-target-unit-details-check-your-answers.component.html',
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
    CompaniesHouseDetailsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsCheckYourAnswersComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
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

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  );

  private readonly targetUnitDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly tuDetails = computed(() =>
    this.targetUnitDetails() ? this.targetUnitDetails() : transformAccountReferenceData(this.accountReferenceData()),
  );

  protected readonly summaryDataOriginal = toVariationTargetUnitDetailsOriginalSummaryData(
    this.accountReferenceData(),
    this.isEditable(),
  );

  protected readonly summaryDataCurrent = toVariationTargetUnitDetailsSummaryData(
    this.targetUnitDetails(),
    this.isEditable(),
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

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const originalAccountReferenceData = (
      this.requestTaskStore.select(
        requestTaskQuery.selectRequestTaskPayload,
      )() as UnderlyingAgreementVariationSubmitRequestTaskPayload
    )?.accountReferenceData;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const currentTUDetails = actionPayload.underlyingAgreementTargetUnitDetails;
    const originalTUDetails = transformAccountReferenceDataToTUDetails(originalAccountReferenceData);
    const areIdentical = areEntitiesIdentical(currentTUDetails, originalTUDetails);

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = areIdentical ? TaskItemStatus.UNCHANGED : TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedPropes = resetReviewSection(reviewProps, REVIEW_TARGET_UNIT_DETAILS_SUBTASK, areIdentical);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedPropes,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
