import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-variation-cancel-task-confirmation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<netz-cancel-confirmation>
    <p class="govuk-body">Your task and all its data has been permanently deleted.</p>
    <p class="govuk-body">You can start this task again at any time.</p>
  </netz-cancel-confirmation> `,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementVariationConfirmationComponent {}
