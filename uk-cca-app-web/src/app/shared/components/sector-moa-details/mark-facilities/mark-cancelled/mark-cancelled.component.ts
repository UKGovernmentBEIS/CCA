import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
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
  selector: 'cca-mark-cancelled',
  templateUrl: './mark-cancelled.component.html',
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
export class MarkCancelledComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorMoaDetailsStore = inject(SectorMoaDetailsStore);

  protected readonly state = this.sectorMoaDetailsStore.stateAsSignal;

  protected readonly selectedTUsCount = computed(() => this.state().selectedTUs.size);

  // only used if 1 target unit selected
  protected readonly selectedTargetUnit = computed(() => {
    const selectedTUs = Array.from(this.state().selectedTUs.values());
    if (selectedTUs.length === 1) return selectedTUs[0];
    return null;
  });

  onSubmit() {
    const idsToSubmit = Array.from(this.state().selectedTUs.values()).map((tu) => tu.moaTargetUnitId);

    this.sectorMoaDetailsStore
      .updateSelectedMarkedFacilities({ status: 'CANCELLED', filterResourceIds: idsToSubmit })
      .subscribe(() => {
        this.sectorMoaDetailsStore.clearSelectedTUs();
        this.router.navigate(['..', 'confirmation', 'cancelled'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
