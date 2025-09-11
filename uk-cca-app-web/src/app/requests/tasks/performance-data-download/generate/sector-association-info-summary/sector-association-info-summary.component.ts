import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

@Component({
  selector: 'cca-sector-association-info-summary',
  template: `
    <div>
      <dl govuk-summary-list>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Sector ID</dt>
          <dd govukSummaryListRowValue>{{ sectorId() }}</dd>
        </div>

        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Sector name</dt>
          <dd govukSummaryListRowValue>{{ sectorName() }}</dd>
        </div>

        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Target period</dt>
          <dd govukSummaryListRowValue>{{ targetPeriod() }}</dd>
        </div>
      </dl>
    </div>
  `,
  standalone: true,
  imports: [SummaryListComponent, SummaryListRowDirective, SummaryListRowKeyDirective, SummaryListRowValueDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorAssociationInfoSummaryComponent {
  protected readonly sectorId = input<string>();
  protected readonly sectorName = input<string>();
  protected readonly targetPeriod = input<string>();
}
