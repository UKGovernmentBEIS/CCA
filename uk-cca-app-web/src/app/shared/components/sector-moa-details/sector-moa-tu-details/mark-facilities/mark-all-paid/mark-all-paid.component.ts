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

import { SectorMoaTUDetailsStore } from '../../sector-moa-tu-details.store';

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
  private readonly sectorMoaTUDetailsStore = inject(SectorMoaTUDetailsStore);

  protected readonly state = this.sectorMoaTUDetailsStore.stateAsSignal;

  ngOnInit() {
    this.sectorMoaTUDetailsStore.clearSelectedFacilities();
  }

  onSubmit() {
    this.sectorMoaTUDetailsStore
      .updateSelectedMarkedFacilities({ status: 'COMPLETED', filterResourceIds: [] })
      .subscribe(() => {
        this.sectorMoaTUDetailsStore.clearSelectedFacilities();
        this.router.navigate(['..', 'confirmation', 'paid'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
