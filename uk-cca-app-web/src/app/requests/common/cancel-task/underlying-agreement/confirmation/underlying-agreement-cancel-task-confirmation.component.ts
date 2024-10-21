import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-cancel-task-confirmation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<netz-cancel-confirmation>
    <p class="govuk-body">The task has been cancelled and the target unit account has been closed.</p>
  </netz-cancel-confirmation> `,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementCancelTaskConfirmationComponent {}
