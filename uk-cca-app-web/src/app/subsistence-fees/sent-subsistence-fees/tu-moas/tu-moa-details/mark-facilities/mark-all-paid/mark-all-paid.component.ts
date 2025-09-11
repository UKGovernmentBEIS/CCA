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

import { TuMoaDetailsStore } from '../../tu-moa-details.store';

@Component({
  selector: 'cca-mark-all-paid',
  templateUrl: './mark-all-paid.component.html',
  standalone: true,
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
  private readonly tuMoaDetailsStore = inject(TuMoaDetailsStore);

  protected readonly state = this.tuMoaDetailsStore.stateAsSignal;

  ngOnInit() {
    this.tuMoaDetailsStore.clearSelectedFacilities();
  }

  onSubmit() {
    this.tuMoaDetailsStore
      .updateSelectedMarkedFacilities({
        status: 'COMPLETED',
        filterResourceIds: [],
      })
      .subscribe(() => {
        this.tuMoaDetailsStore.clearSelectedFacilities();
        this.router.navigate(['..', 'confirmation', 'paid'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
