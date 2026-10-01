import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { FacilityPatReportingTypesPipe } from '@shared/pipes';
import { truncateText } from '@shared/utils';

import { FacilityPATReportStore } from '../../../facility-pat-report.store';

@Component({
  selector: 'cca-pat-details',
  templateUrl: './pat-details.component.html',
  imports: [
    RouterLink,
    PageHeadingComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    FacilityPatReportingTypesPipe,
  ],
})
export class PatDetailsComponent {
  private readonly facilityPATReportStore = inject(FacilityPATReportStore);

  private readonly state = this.facilityPATReportStore.stateAsSignal;

  protected readonly truncateText = truncateText;

  protected readonly reportingYear = computed(() => this.state()?.details?.targetPeriodYear);
  protected readonly actions = computed(() => this.state()?.details?.data?.savingActions);
}
