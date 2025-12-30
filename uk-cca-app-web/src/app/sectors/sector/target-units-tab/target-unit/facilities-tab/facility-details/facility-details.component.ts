import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Params, Router } from '@angular/router';

import { switchMap, take } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ItemLinkPipe } from '@netz/common/pipes';
import { ButtonDirective, TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { FacilityInfoDTO, RequestItemsService, RequestsService } from 'cca-api';

import { AuditSummaryComponent } from '../facility-audit/audit-summary.component';
import { WorkflowHistoryTabComponent } from '../workflow-history-tab/workflow-history-tab.component';
import { toFacilityDetailsSummaryData } from './facility-details-summary-data';

@Component({
  selector: 'cca-facility-details',
  templateUrl: './facility-details.component.html',
  imports: [
    PageHeadingComponent,
    TagComponent,
    SummaryComponent,
    StatusPipe,
    TabsComponent,
    TabLazyDirective,
    AuditSummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    WorkflowHistoryTabComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authStore = inject(AuthStore);
  private readonly requestsService = inject(RequestsService);
  private readonly requestItemsService = inject(RequestItemsService);

  protected readonly roleType = this.authStore.select(selectUserRoleType);
  protected readonly queryParams: Params = { change: true };
  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;

  private readonly availableWorkflows = toSignal(
    this.requestsService.getAvailableWorkflows('FACILITY', String(this.facilityInfoDTO.facilityId)),
  );

  protected readonly isAllowedToTriggerFacilityAudit = computed(
    () => this.availableWorkflows()?.['FACILITY_AUDIT']?.valid,
  );

  protected readonly summaryData = computed(() => toFacilityDetailsSummaryData(this.facilityInfoDTO, this.roleType()));

  onAuditTaskStart() {
    this.requestsService
      .processRequestCreateAction(
        {
          requestType: 'FACILITY_AUDIT',
          requestCreateActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
          },
        },
        String(this.facilityInfoDTO.facilityId),
      )
      .pipe(
        take(1),
        switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
      )
      .subscribe(({ items }) => {
        const link = items?.length == 1 ? new ItemLinkPipe().transform(items[0]) : ['/dashboard'];

        this.router.navigate(link, { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
