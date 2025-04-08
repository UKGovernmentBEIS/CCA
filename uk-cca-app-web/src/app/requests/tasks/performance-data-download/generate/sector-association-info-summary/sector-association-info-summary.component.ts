import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

@Component({
  selector: 'cca-sector-association-info-summary',
  standalone: true,
  imports: [SummaryListComponent, SummaryListRowDirective, SummaryListRowKeyDirective, SummaryListRowValueDirective],
  templateUrl: './sector-association-info-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorAssociationInfoSummaryComponent {
  readonly sectorId = input<string>();
  readonly sectorName = input<string>();
  readonly targetPeriod = input<string>();
}
