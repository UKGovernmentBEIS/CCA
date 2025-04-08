import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { AccountStatusPipe, TargetUnitStatusColorPipe } from '@shared/pipes';
import { toTargetUnitDetailsSummaryData } from '@shared/utils';

import { ActiveTargetUnitStore } from '../active-target-unit.store';
import { FacilitiesListComponent } from './facilities-tab/facilities-list/facilities-list.component';
import { TuReportsTabComponent } from './reports-tab/tu-reports-tab.component';
import { UsersAndContactsTabComponent } from './users-and-contacts-tab/users-and-contacts-tab.component';
import { WorkflowHistoryTabComponent } from './workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-target-unit',
  templateUrl: './target-unit.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    UsersAndContactsTabComponent,
    SummaryComponent,
    WorkflowHistoryTabComponent,
    ButtonDirective,
    RouterLink,
    TargetUnitStatusColorPipe,
    AccountStatusPipe,
    TagComponent,
    FacilitiesListComponent,
    TuReportsTabComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitComponent {
  private readonly authStore = inject(AuthStore);
  private readonly activeTargetUnitStore = inject(ActiveTargetUnitStore);

  private readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly accountDetails = this.activeTargetUnitStore.state;
  private readonly userHasSectorOrRegulatorRole = ['SECTOR_USER', 'REGULATOR'].includes(this.roleType());
  private readonly accountStatusIsNewOrLive = ['NEW', 'LIVE'].includes(
    this.accountDetails?.targetUnitAccountDetails?.status,
  );
  private readonly accountStatusIsTerminated = this.accountDetails?.targetUnitAccountDetails?.status === 'TERMINATED';
  private readonly userIsRegulator = this.roleType() === 'REGULATOR';

  private readonly isEditable = this.userHasSectorOrRegulatorRole && this.accountStatusIsNewOrLive;

  private readonly isFinancialIndependenceEditable =
    this.isEditable || (this.accountStatusIsTerminated && this.userIsRegulator);

  protected readonly summaryData = toTargetUnitDetailsSummaryData(
    this.accountDetails,
    this.isEditable,
    this.isFinancialIndependenceEditable,
    `./${this.accountDetails?.underlyingAgreementDetails?.id}/file-download`,
  );
}
