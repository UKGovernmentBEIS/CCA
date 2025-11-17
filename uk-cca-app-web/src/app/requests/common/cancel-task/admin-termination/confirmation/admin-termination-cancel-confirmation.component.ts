import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-admin-termination-cancel-task-confirmation',
  template: `
    <netz-cancel-confirmation>
      <p>Your task and all its data has been permanently deleted.</p>
      <p>You can start this task again at any time.</p>
    </netz-cancel-confirmation>
  `,
  imports: [CancelConfirmationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationCancelTaskConfirmationComponent {}
