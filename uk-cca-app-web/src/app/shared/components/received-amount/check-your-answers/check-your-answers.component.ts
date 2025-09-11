import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { SectorMoasReceivedAmountStore } from '../received-amount.store';
import { toReceivedAmountSummaryData } from './received-amount-summary';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <netz-page-heading caption="Change">Check your answers</netz-page-heading>
    <cca-summary [data]="data" />
    <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly receivedAmountStore = inject(SectorMoasReceivedAmountStore);

  protected readonly userRoleType = inject(AuthStore).select(selectUserRoleType);

  protected readonly state = this.receivedAmountStore.stateAsSignal;
  protected readonly moaId = +this.activatedRoute.snapshot.paramMap.get('moaId');

  protected readonly data = toReceivedAmountSummaryData(
    this.state().changeType,
    this.state().businessId,
    this.state().transactionId,
    this.state().name,
    this.state().details,
    this.state().receivedAmount,
    this.userRoleType() === 'REGULATOR',
  );

  onSubmit() {
    this.receivedAmountStore.submitReceivedAmount(this.moaId);
    this.router.navigate(['..', 'confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
  }
}
