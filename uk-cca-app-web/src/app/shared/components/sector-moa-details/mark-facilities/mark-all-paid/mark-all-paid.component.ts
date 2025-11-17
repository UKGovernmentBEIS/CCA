import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  WarningTextComponent,
} from '@netz/govuk-components';

import { SectorMoaDetailsStore } from '../../sector-moa-details.store';

@Component({
  selector: 'cca-mark-all-paid',
  templateUrl: './mark-all-paid.component.html',
  imports: [
    PageHeadingComponent,
    WarningTextComponent,
    ButtonDirective,
    PendingButtonDirective,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkAllPaidComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorMoaDetailsStore = inject(SectorMoaDetailsStore);

  protected readonly state = this.sectorMoaDetailsStore.stateAsSignal;

  ngOnInit() {
    this.sectorMoaDetailsStore.clearSelectedTUs();
  }

  onSubmit() {
    this.sectorMoaDetailsStore
      .updateSelectedMarkedFacilities({ status: 'COMPLETED', filterResourceIds: [] })
      .subscribe(() => {
        this.sectorMoaDetailsStore.clearSelectedTUs();
        this.router.navigate(['..', 'confirmation', 'paid'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
