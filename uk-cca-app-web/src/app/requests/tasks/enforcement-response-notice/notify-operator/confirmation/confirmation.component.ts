import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { enforcementResponseNoticeQuery } from '../../enforcement-response-notice.selectors';

@Component({
  selector: 'cca-enforcement-response-notice-notify-operator-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>Enforcement response notice sent to operator</govuk-panel>

        @if (isPenaltyNotice()) {
          <h2 class="govuk-heading-m">What happens next</h2>
          <p class="govuk-body">
            A new task has been created to allow you to provide conclusion about the penalty notice.
          </p>
        }

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly enforcementResponseNotice = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectEnforcementResponseNotice,
  );

  protected readonly isPenaltyNotice = computed(() => this.enforcementResponseNotice()?.type === 'PENALTY');
}
