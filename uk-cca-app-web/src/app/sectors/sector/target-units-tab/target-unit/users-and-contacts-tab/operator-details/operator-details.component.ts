import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { AuthStore, selectUserId, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { SummaryComponent, TwoFaLinkComponent } from '@shared/components';

import { ActiveOperatorStore } from './active-operator.store';
import { toSummaryData } from './operator-details-summary-helper';

@Component({
  selector: 'cca-operator-details',
  template: `
    @if (operatorUserDetails()) {
      <netz-page-heading
        >{{ operatorUserDetails()?.firstName }} {{ operatorUserDetails()?.lastName }}</netz-page-heading
      >
      <cca-summary [data]="summaryData()" />

      @if (isCurrentUser()) {
        <cca-two-fa-link [title]="'Change two factor authentication'" [link]="'/2fa/change'" />
      } @else if (isEditable() && operatorUserId && accountId) {
        <cca-two-fa-link
          [title]="'Reset two-factor authentication'"
          [link]="'/2fa/reset-2fa'"
          [userId]="operatorUserId"
          [accountId]="accountId"
          [userName]="userFullName()"
          [role]="'OPERATOR'"
        />
      }
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent, TwoFaLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly activeOperatorStore = inject(ActiveOperatorStore);
  private readonly authStore = inject(AuthStore);

  private readonly currentUserId = this.authStore.select(selectUserId);
  private readonly roleType = this.authStore.select(selectUserRoleType);
  private readonly operatorState = this.activeOperatorStore.stateAsSignal;

  protected readonly operatorUserId = this.activatedRoute.snapshot.paramMap.get('userId');
  protected readonly accountId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  protected readonly isCurrentUser = computed(() => this.currentUserId() === this.operatorUserId);
  protected readonly isEditable = computed(() => this.operatorState().editable);

  protected readonly operatorUserDetails = computed(() => this.operatorState().details);
  protected readonly userFullName = computed(
    () => `${this.operatorUserDetails()?.firstName} ${this.operatorUserDetails()?.lastName}`,
  );

  protected readonly summaryData = computed(() =>
    toSummaryData(this.operatorUserDetails(), this.roleType() === 'REGULATOR'),
  );
}
