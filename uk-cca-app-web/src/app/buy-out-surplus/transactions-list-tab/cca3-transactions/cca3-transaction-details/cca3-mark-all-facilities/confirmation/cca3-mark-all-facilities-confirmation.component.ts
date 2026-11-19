import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { CCA3_MARK_ALL_CONTENT, Cca3MarkAllStatus } from '../cca3-mark-all-facilities.types';

@Component({
  selector: 'cca-cca3-mark-all-facilities-confirmation',
  template: `
    <div class="govuk-!-width-two-thirds">
      <govuk-panel [title]="content().confirmation" />

      <div class="govuk-!-margin-top-9">
        <a class="govuk-link" [routerLink]="['../../../']" [replaceUrl]="true" [relativeTo]="activatedRoute">
          Return to: Buy-out MoA {{ transactionCode() }}</a
        >
      </div>
    </div>
  `,
  imports: [RouterLink, PanelComponent],
})
export class Cca3MarkAllFacilitiesConfirmationComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly transactionCode = signal<string>(
    this.activatedRoute.snapshot.data.transactionDetails?.transactionCode,
  );

  private readonly status = signal<Cca3MarkAllStatus>(this.activatedRoute.snapshot.data.markAllStatus);
  protected readonly content = computed(() => CCA3_MARK_ALL_CONTENT[this.status()]);
}
