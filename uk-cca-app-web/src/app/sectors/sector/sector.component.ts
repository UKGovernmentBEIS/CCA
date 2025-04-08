import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { ActiveSectorStore } from './active-sector.store';
import { SectorContactsTabComponent } from './contacts-tab/sector-contacts-tab.component';
import { SectorDetailsTabComponent } from './details-tab/sector-details-tab.component';
import { PerformanceDataReportsComponent } from './reports-tab/performance-data-report/performance-data-reports.component';
import { SectorSchemeTabComponent } from './scheme-tab/sector-scheme-tab.component';
import { SubsistenceFeesTabComponent } from './subsistence-fees-tab/subsistence-fees-tab.component';
import { SectorTargetUnitsTabComponent } from './target-units-tab/target-units-tab.component';
import { WorkflowHistoryTabComponent } from './target-units-tab/workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-sector',
  templateUrl: './sector.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    SectorDetailsTabComponent,
    SectorSchemeTabComponent,
    SectorContactsTabComponent,
    SectorTargetUnitsTabComponent,
    ButtonDirective,
    RouterLink,
    PerformanceDataReportsComponent,
    WorkflowHistoryTabComponent,
    SubsistenceFeesTabComponent,
  ],
})
export class SectorComponent {
  sectorDetails = inject(ActiveSectorStore).state;
  title = `${this.sectorDetails.sectorAssociationDetails.acronym} - ${this.sectorDetails.sectorAssociationDetails.commonName}`;
}
