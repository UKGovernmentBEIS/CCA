import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { SummaryComponent } from '@shared/components';

import { ActiveOperatorStore } from './active-operator.store';
import { toSummaryData } from './operator-details-summary-helper';

@Component({
  selector: 'cca-operator-details',
  template: `
    @if (operatorUserDetails) {
      <netz-page-heading>{{ operatorUserDetails.firstName }} {{ operatorUserDetails.lastName }}</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <a class="govuk-link" routerLink="/2fa/change" fragment="target-units"> Reset two factor authentication </a>
    }
  `,
  standalone: true,
  imports: [RouterLink, PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  private readonly activeOperatorStore = inject(ActiveOperatorStore);
  private readonly authStore = inject(AuthStore);

  private readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly operatorUserDetails = this.activeOperatorStore.state.details;

  protected readonly summaryData = toSummaryData(this.operatorUserDetails, this.roleType() === 'REGULATOR');
}
