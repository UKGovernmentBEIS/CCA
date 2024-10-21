import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { ButtonDirective, TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { AccountStatusPipe, TargetUnitStatusColorPipe } from '@shared/pipes';
import { toTargetUnitDetailsSummaryData } from '@shared/utils';

import { ActiveTargetUnitStore } from '../active-target-unit.store';
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
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitComponent {
  private readonly authStore = inject(AuthStore);
  private readonly activeTargetUnitStore = inject(ActiveTargetUnitStore);

  private readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly accountDetails = this.activeTargetUnitStore.state;

  summaryData = toTargetUnitDetailsSummaryData(
    this.accountDetails,
    ['SECTOR_USER', 'REGULATOR'].includes(this.roleType()) &&
      ['NEW', 'LIVE'].includes(this.accountDetails?.targetUnitAccountDetails?.status),
    `./${this.accountDetails?.underlyingAgreementDetails?.id}/file-download`,
  );
}
