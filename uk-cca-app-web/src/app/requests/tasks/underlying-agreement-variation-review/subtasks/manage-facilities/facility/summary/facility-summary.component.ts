import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isCCA3Scheme,
  toFacilityWizardSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  templateUrl: './facility-summary.component.html',
  imports: [SummaryComponent, PageHeadingComponent, HighlightDiffComponent, NgTemplateOutlet, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestTaskStore.select(
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId)),
  );

  private readonly originalFacility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalFacility(this.facilityId))(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.requestTaskStore.select(
      underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )(),
  );

  private readonly decision = this.requestTaskStore.select(
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
            ? this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)()
            : this.requestTaskStore.select(
                underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
              )(),
        review: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
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
        submit: this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
        review: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}
