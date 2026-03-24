import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { StatusPipe } from '@shared/pipes';

import { FacilityInfoDTO } from 'cca-api';

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
    RouterLink,
    WorkflowHistoryTabComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);

  protected readonly roleType = this.authStore.select(selectUserRoleType);
  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;

  protected readonly summaryData = computed(() => toFacilityDetailsSummaryData(this.facilityInfoDTO, this.roleType()));
}
