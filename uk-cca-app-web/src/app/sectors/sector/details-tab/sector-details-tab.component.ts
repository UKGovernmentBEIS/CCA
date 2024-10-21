import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Params, RouterLink } from '@angular/router';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { ActiveSectorStore } from '../active-sector.store';
import { SectorEnergyEprFactorPipe } from '../pipes/sector-energy-epr-factor.pipe';

@Component({
  selector: 'cca-sector-details-tab',
  templateUrl: './sector-details-tab.component.html',
  standalone: true,
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    RouterLink,
    SectorEnergyEprFactorPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorDetailsTabComponent {
  sectorDetails = inject(ActiveSectorStore).state;
  changeLink = this.sectorDetails.editable;

  queryParams: Params = { change: true };
}
