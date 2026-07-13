import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { SummaryComponent } from '@shared/components';

import { SubsectorAssociationSchemesDTO } from 'cca-api';

import { toSubsectorSchemeSummaryData } from '../scheme-summary-data';

@Component({
  selector: 'cca-sub-sector-details',
  template: `
    <netz-page-heading caption="Subsectors" size="l" data-test-id="heading">{{ subsector()?.name }}</netz-page-heading>
    <cca-summary [data]="schemeSummaryData()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubSectorDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly routeData = signal(
    this.activatedRoute.snapshot.data as { subSector: SubsectorAssociationSchemesDTO },
  );

  protected readonly subsector = computed(() => this.routeData().subSector);

  protected readonly schemeSummaryData = computed(() => {
    const subsector = this.subsector();
    return subsector ? toSubsectorSchemeSummaryData(subsector) : [];
  });
}
