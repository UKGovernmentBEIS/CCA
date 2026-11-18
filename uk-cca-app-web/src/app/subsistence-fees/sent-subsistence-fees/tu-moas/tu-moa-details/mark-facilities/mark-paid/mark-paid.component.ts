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

import { TuMoaDetailsStore } from '../../tu-moa-details.store';

@Component({
  selector: 'cca-mark-paid',
  templateUrl: './mark-paid.component.html',
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
export class MarkPaidComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tuMoaDetailsStore = inject(TuMoaDetailsStore);

  protected readonly state = this.tuMoaDetailsStore.stateAsSignal;

  protected readonly selectedFacilitiessCount = computed(() => this.state().selectedFacilities.size);

  // only used if 1 facility is selected
  protected readonly selectedFacility = computed(() => {
    const selectedFacilities = Array.from(this.state().selectedFacilities.values());
    if (selectedFacilities.length === 1) return selectedFacilities[0];
    return null;
  });

  onSubmit() {
    const idsToSubmit = Array.from(this.state().selectedFacilities.values()).map((sf) => sf.moaFacilityId);

    this.tuMoaDetailsStore
      .updateSelectedMarkedFacilities({
        status: 'COMPLETED',
        filterResourceIds: idsToSubmit,
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
