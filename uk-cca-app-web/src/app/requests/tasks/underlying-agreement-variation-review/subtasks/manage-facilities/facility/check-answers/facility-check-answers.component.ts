import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  toFacilityWizardSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../../transform';
import { resetDetermination } from '../../../../utils';

@Component({
  selector: 'cca-facility-check-answers',
  templateUrl: './facility-check-answers.component.html',
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    RouterLink,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilityCheckAnswersComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly originalFacility = computed(() =>
    this.store.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.store.select(underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()))(),
  );

  private readonly areIdentical = areEntitiesIdentical(this.facility(), this.originalFacility());

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  protected readonly summaryDataOriginal = computed(() =>
    toFacilityWizardSummaryDataWithDecision(
      this.facility().status === 'NEW' ? this.facility() : this.originalFacility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
      this.decision,
      {
        submit:
          this.facility().status === 'NEW'
            ? this.store.select(underlyingAgreementQuery.selectAttachments)()
            : this.store.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
        review: this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );

  protected readonly summaryDataCurrent = computed(() =>
    toFacilityWizardSummaryDataWithDecision(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
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
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = this.areIdentical ? TaskItemStatus.UNCHANGED : TaskItemStatus.COMPLETED;
    });

    const reviewSectionsCompleted = produce(payload.reviewSectionsCompleted, (draft) => {
      draft[this.facilityId] = this.areIdentical
        ? TaskItemStatus.UNCHANGED
        : this.decision.type === 'ACCEPTED'
          ? TaskItemStatus.ACCEPTED
          : TaskItemStatus.REJECTED;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const determination = resetDetermination(
      this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
    );

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: payload.reviewGroupDecisions,
      facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute });
    });
  }
}
