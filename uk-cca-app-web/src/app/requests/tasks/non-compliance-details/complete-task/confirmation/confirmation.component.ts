import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { nonComplianceDetailsQuery } from '../../non-compliance-details.selectors';

@Component({
  selector: 'cca-non-compliance-confirmation',
  template: `
    <govuk-panel title="Non-compliance details completed"></govuk-panel>

    @if (isEnforcementResponseNoticeRequired()) {
      <h3 class="govuk-heading-m">What happens next</h3>
      <p>A new task has been created to allow you to upload and send the Notice of Intent</p>
    }

    <netz-return-to-task-or-action-page />
  `,
  imports: [PanelComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly nonComplianceDetails = this.requestTaskStore.select(
    nonComplianceDetailsQuery.selectNonComplianceDetails,
  );

  protected readonly isEnforcementResponseNoticeRequired = computed(
    () => this.nonComplianceDetails()?.isEnforcementResponseNoticeRequired === true,
  );
}
