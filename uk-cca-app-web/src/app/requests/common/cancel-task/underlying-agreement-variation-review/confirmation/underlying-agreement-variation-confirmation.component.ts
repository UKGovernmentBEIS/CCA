import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-variation-cancel-task-confirmation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: ` <netz-cancel-confirmation> </netz-cancel-confirmation> `,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementVariationConfirmationComponent {}
