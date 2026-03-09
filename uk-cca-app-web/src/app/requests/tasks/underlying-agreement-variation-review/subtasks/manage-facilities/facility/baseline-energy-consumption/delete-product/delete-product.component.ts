import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import { BaselineEnergyDraftService, underlyingAgreementQuery } from '@requests/common';

@Component({
  selector: 'cca-delete-product',
  template: `
    <netz-page-heading [caption]="facility()?.facilityDetails?.name">
      Are you sure you want to delete {{ this.productName }}?
    </netz-page-heading>

    <p>
      When you submit the "Baseline energy or carbon consumption" page, your product and all its data will be deleted
      permanently.
    </p>

    <govuk-warning-text assistiveText="">You will not be able to undo this action.</govuk-warning-text>
    <button (click)="onDelete()" class="govuk-button govuk-button--warning">Delete product</button>
  `,
  imports: [WarningTextComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly draftService = inject(BaselineEnergyDraftService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly productName = this.activatedRoute.snapshot.paramMap.get('productName');

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onDelete() {
    // Remove product from draft service (NO API CALL)
    this.draftService.removeProduct(this.productName);

    // Navigate back to parent
    this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
  }
}
