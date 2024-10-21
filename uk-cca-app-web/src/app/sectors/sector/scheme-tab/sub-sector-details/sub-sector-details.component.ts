import { DecimalPipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import {
  GovukTableColumn,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TableComponent,
} from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';

import { SubsectorAssociationSchemeDTO } from 'cca-api';

@Component({
  selector: 'cca-sub-sector-details',
  templateUrl: './sub-sector-details.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TableComponent,
    DecimalPipe,
    PercentPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubSectorDetailsComponent {
  activatedRoute = inject(ActivatedRoute);

  routeData = this.activatedRoute.snapshot.data as { subSector: SubsectorAssociationSchemeDTO };

  title = this.routeData.subSector.subsectorAssociation.name;

  sectorCommitmentColumns: GovukTableColumn[] = [
    { field: 'targetPeriod', header: 'Target period', widthClass: 'govuk-!-width-one-third' },
    { field: 'targetImprovement', header: 'Target improvement' },
  ];

  sectorCommitment = this.routeData.subSector.targetSet?.targetCommitments;
}
