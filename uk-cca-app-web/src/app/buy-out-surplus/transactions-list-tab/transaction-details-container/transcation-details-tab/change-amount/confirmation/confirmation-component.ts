import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <govuk-panel title="Current amount updated" />

    <div class="govuk-!-margin-top-9">
      <a class="govuk-link" [routerLink]="['../../']" [replaceUrl]="true" [relativeTo]="activatedRoute">
        Return to: Buy-out MoA {{ transactionCode }}</a
      >
    </div>
  `,
  imports: [RouterLink, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly transactionCode = this.activatedRoute.snapshot.data.transactionDetails?.transactionCode;
}
