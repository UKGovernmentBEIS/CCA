import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';

import { ActiveSectorStore } from './active-sector.store';
import { SectorContactsTabComponent } from './contacts-tab/sector-contacts-tab.component';
import { SectorDetailsTabComponent } from './details-tab/sector-details-tab.component';
import { SectorSchemeTabComponent } from './scheme-tab/sector-scheme-tab.component';
import { SectorTargetUnitsTabComponent } from './target-units-tab/target-units-tab.component';

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
  ],
})
export class SectorComponent {
  sectorDetails = inject(ActiveSectorStore).state;
  title = `${this.sectorDetails.sectorAssociationDetails.acronym} - ${this.sectorDetails.sectorAssociationDetails.commonName}`;
}
