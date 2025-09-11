import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  isCCA3Scheme,
  TaskItemStatus,
  TasksApiService,
  toFacilityWizardSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../transform';

@Component({
  selector: 'cca-facility-check-answers',
  template: `
    @if (facility(); as facility) {
      <netz-page-heading [caption]="facility.facilityDetails.name">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData()" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" [routerLink]="['../../']"> Return to: Manage facilities </a>
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, PendingButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilityCheckAnswersComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
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

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
    });
  }
}
