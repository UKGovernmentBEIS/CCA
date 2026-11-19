import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import {
  ButtonDirective,
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TabLazyDirective,
  TabsComponent,
  TagComponent,
} from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';
import { CountryService } from '@shared/services';

import { FacilityInfoDTO } from 'cca-api';

import { CertificationStatusTabComponent } from '../certification-status-tab/certification-status-tab.component';
import { AuditSummaryComponent } from '../facility-audit/audit-summary.component';
import { FacilityReportsTabComponent } from '../reports-tab/facility-reports-tab.component';
import { SurplusAmountPipe } from '../surplus-amount.pipe';
import { WorkflowHistoryTabComponent } from '../workflow-history-tab/workflow-history-tab.component';
import { toFacilityDetailsSummaryData } from './facility-details-summary-data';

@Component({
  selector: 'cca-facility-details',
  templateUrl: './facility-details.component.html',
  imports: [
    PageHeadingComponent,
    TagComponent,
    SummaryComponent,
    StatusColorPipe,
    StatusPipe,
    TabsComponent,
    TabLazyDirective,
    CertificationStatusTabComponent,
    AuditSummaryComponent,
    ButtonDirective,
    RouterLink,
    WorkflowHistoryTabComponent,
    FacilityReportsTabComponent,
    SurplusAmountPipe,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
})
export class FacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);
  private readonly countryService = inject(CountryService);

  private readonly roleType = this.authStore.select(selectUserRoleType);
  private readonly routeData = toSignal(this.activatedRoute.data, { initialValue: this.activatedRoute.snapshot.data });

  protected readonly facilityInfoDTO = computed(() => this.routeData().facilityDetails as FacilityInfoDTO);
  protected readonly surplusDetails = computed(() => this.facilityInfoDTO().surplusDetails);
  protected readonly canEditSurplus = computed(
    () => this.roleType() === 'REGULATOR' && this.surplusDetails()?.schemeVersion === 'CCA_3',
  );
  protected readonly canViewSurplusHistory = computed(
    () => this.surplusDetails()?.schemeVersion === 'CCA_3' && !!this.surplusDetails()?.hasHistory,
  );
  protected readonly showSurplusActions = computed(() => this.canEditSurplus() || this.canViewSurplusHistory());
  protected readonly showAudit = computed(() => this.roleType() !== 'SECTOR_USER');

  protected readonly summaryData = computed(() =>
    toFacilityDetailsSummaryData(this.facilityInfoDTO(), this.roleType(), this.countryService.countries()),
  );
}
