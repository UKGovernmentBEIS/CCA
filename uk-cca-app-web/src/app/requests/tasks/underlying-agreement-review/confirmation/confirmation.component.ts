import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';
import { underlyingAgreementReviewQuery } from '@requests/common';

@Component({
  selector: 'cca-confirmation',
  standalone: true,
  template: `
    <govuk-panel>Application {{ determinationType === 'ACCEPTED' ? 'accepted' : 'rejected' }}</govuk-panel>
    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PanelComponent, RouterLink],
})
export class NotifyOperatorConfirmationComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly determinationType = this.store.select(underlyingAgreementReviewQuery.selectDetermination)().type;
}
