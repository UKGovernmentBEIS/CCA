import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { WarningTextComponent } from '@netz/govuk-components';
import { BaselineEnergyDraftService, underlyingAgreementQuery } from '@requests/common';

@Component({
  selector: 'cca-exclude-product',
  template: `
    <netz-page-heading [caption]="facility()?.facilityDetails?.name">
      Are you sure you want to exclude {{ productName }}?
    </netz-page-heading>

    <p>Your product and all its data will remain available but will be marked as excluded.</p>

    <govuk-warning-text assistiveText=""
      >The product will be treated as excluded and removed from all baseline calculations.</govuk-warning-text
    >

    <button (click)="onExclude()" class="govuk-button govuk-button--warning">Exclude product</button>
  `,
  imports: [WarningTextComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExcludeProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly draftService = inject(BaselineEnergyDraftService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  protected readonly productName = this.activatedRoute.snapshot.paramMap.get('productName');

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  onExclude() {
    // Mark product as excluded in draft service (NO API CALL)
    this.draftService.excludeProduct(this.productName);

    // Navigate back to parent
    this.router.navigate(['../..'], { relativeTo: this.activatedRoute });
  }
}
