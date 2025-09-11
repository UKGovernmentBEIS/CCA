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

import { SectorMoaTUDetailsStore } from '../../sector-moa-tu-details.store';

@Component({
  selector: 'cca-mark-in-progress',
  templateUrl: './mark-in-progress.component.html',
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
export class MarkInProgressComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorMoaTUDetailsStore = inject(SectorMoaTUDetailsStore);

  protected readonly state = this.sectorMoaTUDetailsStore.stateAsSignal;

  protected readonly selectedFacilitiessCount = computed(() => this.state().selectedFacilities.size);

  // only used if 1 facility is selected
  protected readonly selectedFacility = computed(() => {
    const selectedFacilities = Array.from(this.state().selectedFacilities.values());
    if (selectedFacilities.length === 1) return selectedFacilities[0];
    return null;
  });

  onSubmit() {
    const idsToSubmit = Array.from(this.state().selectedFacilities.values()).map((sf) => sf.moaFacilityId);

    this.sectorMoaTUDetailsStore
      .updateSelectedMarkedFacilities({ status: 'IN_PROGRESS', filterResourceIds: idsToSubmit })
      .subscribe(() => {
        this.sectorMoaTUDetailsStore.clearSelectedFacilities();
        this.router.navigate(['..', 'confirmation', 'in-progress'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
