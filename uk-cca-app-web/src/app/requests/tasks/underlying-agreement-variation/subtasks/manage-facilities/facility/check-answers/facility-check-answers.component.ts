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
  resetFacilityNonComparisonFields,
  TaskItemStatus,
  TasksApiService,
  toFacilitySummaryDataWithStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../../transform';
import { extractReviewProps, resetFacilityReviewSection } from '../../../../utils';

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
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly originalFacility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.facility()?.facilityDetails?.participatingSchemeVersions)
      ? SchemeVersion.CCA_3
      : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.requestTaskStore.select(
      underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )(),
  );

  protected readonly summaryDataOriginal = computed(() =>
    toFacilitySummaryDataWithStatus(
      this.facility().status === 'NEW' ? this.facility() : this.originalFacility(),
      this.sectorSchemeData(),
      this.facility()?.facilityDetails?.participatingSchemeVersions,
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
      this.sectorSchemeData(),
      this.facility()?.facilityDetails?.participatingSchemeVersions,
      this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(
        requestTaskQuery.selectRequestTaskPayload,
      )() as UnderlyingAgreementVariationSubmitRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const currentFacility = actionPayload.facilities?.find((f) => f.facilityId === this.facilityId);
    const originalFacility = originalPayload?.underlyingAgreement?.facilities?.find(
      (f) => f.facilityId === this.facilityId,
    );

    const areIdentical = originalFacility
      ? areEntitiesIdentical(
          resetFacilityNonComparisonFields(currentFacility),
          resetFacilityNonComparisonFields(originalFacility),
        )
      : false;

    const currentSectionsCompleted =
      this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)() || {};

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = areIdentical ? TaskItemStatus.UNCHANGED : TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewProps = extractReviewProps(this.requestTaskStore);
    const resetedProps = resetFacilityReviewSection(reviewProps, this.facilityId, areIdentical);

    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted, {
      ...reviewProps,
      ...resetedProps,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
    });
  }
}
