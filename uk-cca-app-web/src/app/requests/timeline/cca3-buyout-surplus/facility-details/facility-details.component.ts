import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryComponent } from '@shared/components';

import { toBuyoutSurplusFacilitySummaryData } from './buyout-surplus-facility-summary-data';
import { FacilityCalculationPresentation } from './facility-calculation.types';
import { facilityCalculationHelp } from './facility-calculation-help';
import { HistoricalFacilityDetails } from './facility-details.resolver';

export interface FacilityDetailsRouteData {
  facilityDetails: HistoricalFacilityDetails;
  facilityCalculation: FacilityCalculationPresentation;
}

@Component({
  selector: 'cca-buyout-surplus-facility-details',
  templateUrl: './facility-details.component.html',
  imports: [PageHeadingComponent, GovukDatePipe, SummaryComponent],
})
export class FacilityDetailsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly data = toSignal(this.route.data, { initialValue: this.route.snapshot.data });
  protected readonly view = computed(() => {
    const { facilityDetails, facilityCalculation } = this.data() as FacilityDetailsRouteData;
    if (!facilityDetails || !facilityCalculation) return undefined;
    return {
      siteName: facilityDetails.facility.siteName,
      creationDate: facilityDetails.creationDate,
      summary: toBuyoutSurplusFacilitySummaryData(facilityDetails, facilityCalculation),
      help: facilityCalculationHelp(facilityCalculation.scenario),
    };
  });
}
