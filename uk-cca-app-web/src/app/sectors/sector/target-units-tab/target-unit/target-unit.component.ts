import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { ActiveTargetUnitStore } from '../active-target-unit.store';
import { BuyoutAndSurplusTabComponent } from './buyout-and-surplus-tab/buyout-and-surplus-tab.component';
import { DetailsSummaryComponent, IsEditableData } from './details-summary/details-summary.component';
import { FacilitiesListComponent } from './facilities-tab/facilities-list/facilities-list.component';
import { TuReportsTabComponent } from './reports-tab/tu-reports-tab.component';
import { UsersAndContactsTabComponent } from './users-and-contacts-tab/users-and-contacts-tab.component';
import { WorkflowHistoryTabComponent } from './workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-target-unit',
  templateUrl: './target-unit.component.html',
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    UsersAndContactsTabComponent,
    WorkflowHistoryTabComponent,
    ButtonDirective,
    RouterLink,
    StatusColorPipe,
    StatusPipe,
    TagComponent,
    FacilitiesListComponent,
    TuReportsTabComponent,
    BuyoutAndSurplusTabComponent,
    DetailsSummaryComponent,
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

  protected readonly isEditableData: IsEditableData = {
    isEditable: this.isEditable,
    isFinancialIndependenceEditable: this.isFinancialIndependenceEditable,
  };
}
