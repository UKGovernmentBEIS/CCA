import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementVariationActivatedRequestActionPayload } from 'cca-api';

import { underlyingAgreementVariationActivatedQuery } from './+state/underlying-agreement-variation-activated.selectors';
import { toUnderlyingAgreementVariationActivatedSummaryData } from './underlying-agreement-variation-activated-summary-data';

@Component({
  selector: 'cca-underlying-agreement-variation-activated',
  standalone: true,
  imports: [SummaryComponent],
  template: ` <cca-summary [data]="summaryData()" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationActivatedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  readonly activatedPayload: Signal<UnderlyingAgreementVariationActivatedRequestActionPayload> = computed(() => {
    return {
      officialNotice: this.requestActionStore.select(underlyingAgreementVariationActivatedQuery.selectOfficialNotice)(),
      usersInfo: this.requestActionStore.select(underlyingAgreementVariationActivatedQuery.selectUsersInfo)(),
      defaultContacts: this.requestActionStore.select(
        underlyingAgreementVariationActivatedQuery.selectDefaultContacts,
      )(),
      decisionNotification: this.requestActionStore.select(
        underlyingAgreementVariationActivatedQuery.selectDecisionNotification,
      )(),
      underlyingAgreementDocument: this.requestActionStore.select(
        underlyingAgreementVariationActivatedQuery.selectUnderlyingAgreementDocument,
      )(),
      underlyingAgreementActivationDetails: this.requestActionStore.select(
        underlyingAgreementVariationActivatedQuery.selectUnderlyingAgreementActivationDetails,
      )(),
      underlyingAgreementActivationAttachments: this.requestActionStore.select(
        underlyingAgreementVariationActivatedQuery.selectUnderlyingAgreementActivationAttachments,
      )(),
    } as UnderlyingAgreementVariationActivatedRequestActionPayload;
  });

  readonly summaryData = computed(() => toUnderlyingAgreementVariationActivatedSummaryData(this.activatedPayload()));
}
