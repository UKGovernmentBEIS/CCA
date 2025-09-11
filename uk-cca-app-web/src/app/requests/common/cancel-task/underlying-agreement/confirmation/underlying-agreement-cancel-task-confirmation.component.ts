import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-cancel-task-confirmation',
  template: `
    <netz-cancel-confirmation>
      <p>The task has been cancelled and the target unit account has been closed.</p>
    </netz-cancel-confirmation>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementCancelTaskConfirmationComponent {}
