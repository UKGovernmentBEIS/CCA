import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';

@Component({
  selector: 'cca-variation-confirmation',
  template: `
    <govuk-panel>Variation {{ decision }}</govuk-panel>
    <p>You have {{ decision }} the underlying agreement variation.</p>
    <p>The selected users will receive an email notification of your decision.</p>
    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PanelComponent, RouterLink],
})
export class NotifyOperatorVariationConfirmationComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly decision =
    this.store.select(underlyingAgreementReviewQuery.selectDetermination)().type === 'ACCEPTED'
      ? 'approved'
      : 'rejected';
}
