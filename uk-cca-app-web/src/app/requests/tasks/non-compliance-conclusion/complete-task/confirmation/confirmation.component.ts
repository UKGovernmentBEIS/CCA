import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { nonComplianceConclusionQuery } from '../../non-compliance-conclusion.selectors';

@Component({
  selector: 'cca-conclusion-confirmation',
  template: `
    <govuk-panel title="Conclusion of non-compliance complete"></govuk-panel>

    @if (isPenaltyReissue()) {
      <h2 class="govuk-heading-m">What happens next</h2>
      <p>You can now reissue a replacement penalty notice to the operator from your task list.</p>
    }

    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConclusionConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly isPenaltyReissue = this.requestTaskStore.select(
    nonComplianceConclusionQuery.selectIsPenaltyReissue,
  );
}
