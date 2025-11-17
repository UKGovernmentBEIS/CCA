import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-cca3-migration-account-activation-cancel-confirmation',
  template: `<netz-cancel-confirmation />`,
  imports: [CancelConfirmationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3MigrationAccountActivationCancelConfirmationComponent {}
