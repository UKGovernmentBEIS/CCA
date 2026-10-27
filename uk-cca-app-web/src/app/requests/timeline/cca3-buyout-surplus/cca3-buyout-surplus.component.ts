import { Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { toBuyoutSurplusAccountSummaryData } from './buyout-surplus-account-summary-data';
import { FacilitiesListComponent } from './facilities-list.component';
import { toHistoricalFacilities } from './historical-facilities';

@Component({
  selector: 'cca-cca3-buyout-surplus',
  template: `
    @if (payload(); as payload) {
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <cca-summary [data]="summary()" />
        </div>
      </div>
      <cca-buyout-surplus-facilities-list [facilities]="facilities()" [facilityDetailsAvailable]="true" />
    }
  `,
  imports: [SummaryComponent, FacilitiesListComponent],
})
export class Cca3BuyoutSurplusComponent {
  protected readonly action = inject(RequestActionStore).select(requestActionQuery.selectAction);
  protected readonly payload = computed(
    () => this.action()?.payload as BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload | undefined,
  );
  protected readonly summary = computed(() => toBuyoutSurplusAccountSummaryData(this.payload()));
  protected readonly facilities = computed(() => toHistoricalFacilities(this.payload()));
}
