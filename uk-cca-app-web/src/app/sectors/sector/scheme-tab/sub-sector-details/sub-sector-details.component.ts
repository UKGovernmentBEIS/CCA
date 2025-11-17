import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { SummaryComponent } from '@shared/components';

import { SubsectorAssociationSchemesDTO } from 'cca-api';

import { toSubsectorSchemeSummaryData } from '../scheme-summary-data';

@Component({
  selector: 'cca-sub-sector-details',
  templateUrl: './sub-sector-details.component.html',
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubSectorDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly routeData = this.activatedRoute.snapshot.data as { subSector: SubsectorAssociationSchemesDTO };

  protected readonly subsectorName = this.routeData.subSector.name;

  protected readonly schemeSummaryData = computed(() => {
    const subsector = this.routeData.subSector;
    return subsector ? toSubsectorSchemeSummaryData(subsector) : [];
  });
}
