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
  filterFieldsWithFalsyValues,
  TaskItemStatus,
  TasksApiService,
  toVariationReviewTargetUnitDetailsSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  transformAccountReferenceData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { transformAddress } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { CompaniesInformationService, CompanyProfileDTO } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './review-target-unit-details-check-your-answers.component.html',
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
    CompaniesHouseDetailsComponent,
    ReactiveFormsModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsCheckYourAnswersComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);
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
      address: typeof response === 'object' ? transformAddress(response?.address).join('\n') : null,
    };
  });

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  private readonly accountReferenceData = this.store.select(underlyingAgreementQuery.selectAccountReferenceData)();
  private readonly originalTargetUnitDetails = transformAccountReferenceData(this.accountReferenceData);
  private readonly currentTargetUnitDetails = this.store.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  );

  protected readonly tuDetails = computed(() =>
    this.currentTargetUnitDetails() ? this.currentTargetUnitDetails() : this.originalTargetUnitDetails,
  );

  private readonly areIdentical = areEntitiesIdentical(
    filterFieldsWithFalsyValues(this.currentTargetUnitDetails()),
    filterFieldsWithFalsyValues(this.originalTargetUnitDetails),
  );

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
  )();

  protected readonly summaryDataOriginal = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.originalTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  protected readonly summaryDataCurrent = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.currentTargetUnitDetails(),
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  ngOnInit() {
    if (this.tuDetails().companyRegistrationNumber) {
      this.companiesInformationService
        .getCompanyProfileByRegistrationNumber(this.tuDetails().companyRegistrationNumber)
        .pipe(
          take(1),
          catchError(() => of(null)),
        )
        .subscribe((res) => {
          if (typeof res === 'object') {
            this.companiesHouseDetailsResponse.set(res);
          }
        });
    }
  }

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const sectionsCompleted = produce(
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
      },
    );

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = this.areIdentical
          ? TaskItemStatus.UNCHANGED
          : this.decision.type === 'ACCEPTED'
            ? TaskItemStatus.ACCEPTED
            : TaskItemStatus.REJECTED;
      },
    );

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: payload.reviewGroupDecisions,
      facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
