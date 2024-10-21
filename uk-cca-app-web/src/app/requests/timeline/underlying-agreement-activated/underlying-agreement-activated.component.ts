import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementActivatedRequestActionPayload } from 'cca-api';

import { underlyingAgreementActivatedQuery } from './+state/underlying-agreement-activated.selectors';
import { toUnderlyingAgreementActivatedSummaryData } from './underlying-agreement-activated-summary-data';

@Component({
  selector: 'cca-underlying-agreement-activated',
  standalone: true,
  imports: [SummaryComponent],
  template: ` <cca-summary [data]="summaryData()" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementActivatedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  readonly activatedPayload: Signal<UnderlyingAgreementActivatedRequestActionPayload> = computed(() => {
    return {
      officialNotice: this.requestActionStore.select(underlyingAgreementActivatedQuery.selectOfficialNotice)(),
      usersInfo: this.requestActionStore.select(underlyingAgreementActivatedQuery.selectUsersInfo)(),
      defaultContacts: this.requestActionStore.select(underlyingAgreementActivatedQuery.selectDefaultContacts)(),
      decisionNotification: this.requestActionStore.select(
        underlyingAgreementActivatedQuery.selectDecisionNotification,
      )(),
      underlyingAgreementDocument: this.requestActionStore.select(
        underlyingAgreementActivatedQuery.selectUnderlyingAgreementDocument,
      )(),
      underlyingAgreementActivationDetails: this.requestActionStore.select(
        underlyingAgreementActivatedQuery.selectUnderlyingAgreementActivationDetails,
      )(),
      underlyingAgreementActivationAttachments: this.requestActionStore.select(
        underlyingAgreementActivatedQuery.selectUnderlyingAgreementActivationAttachments,
      )(),
    } as UnderlyingAgreementActivatedRequestActionPayload;
  });
  readonly summaryData = computed(() => toUnderlyingAgreementActivatedSummaryData(this.activatedPayload()));
}
