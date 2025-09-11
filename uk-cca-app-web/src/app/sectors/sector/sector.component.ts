import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent } from '@netz/govuk-components';

import { ActiveSectorStore } from './active-sector.store';
import { SectorContactsTabComponent } from './contacts-tab/sector-contacts-tab.component';
import { SectorDetailsTabComponent } from './details-tab/sector-details-tab.component';
import { ReportsTabComponent } from './reports-tab/reports-tab.component';
import { SectorSchemeTabComponent } from './scheme-tab/sector-scheme-tab.component';
import { SubsistenceFeesTabComponent } from './subsistence-fees-tab/subsistence-fees-tab.component';
import { SectorTargetUnitsTabComponent } from './target-units-tab/target-units-tab.component';
import { WorkflowHistoryTabComponent } from './workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-sector',
  templateUrl: './sector.component.html',
  standalone: true,
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
    WorkflowHistoryTabComponent,
    SubsistenceFeesTabComponent,
    ReportsTabComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorComponent {
  private readonly authStore = inject(AuthStore);

  protected readonly sectorDetails = inject(ActiveSectorStore).state;
  protected readonly isAllowedUser = ['REGULATOR', 'SECTOR_USER'].includes(this.authStore.select(selectUserRoleType)());
  protected readonly title = `${this.sectorDetails.sectorAssociationDetails.acronym} - ${this.sectorDetails.sectorAssociationDetails.commonName}`;
}
