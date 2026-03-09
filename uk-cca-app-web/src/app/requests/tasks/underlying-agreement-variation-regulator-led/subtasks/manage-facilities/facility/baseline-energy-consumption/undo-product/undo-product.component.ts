import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import { BaselineEnergyDraftService, underlyingAgreementQuery } from '@requests/common';

@Component({
  selector: 'cca-undo-product',
  template: `
    <netz-page-heading [caption]="facility()?.facilityDetails?.name">
      Are you sure you want to undo the exclusion for {{ productName }}?
    </netz-page-heading>

    <p>The product will be reinstated as live and included when calculating targets.</p>

    <govuk-warning-text assistiveText="">Only undo if the product should remain in scope.</govuk-warning-text>

    <button (click)="onUndo()" class="govuk-button">Undo exclusion</button>
  `,
  imports: [WarningTextComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UndoProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly draftService = inject(BaselineEnergyDraftService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly productName = this.activatedRoute.snapshot.paramMap.get('productName');

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onUndo() {
    this.draftService.undoExcludeProduct(this.productName);

    this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
  }
}
