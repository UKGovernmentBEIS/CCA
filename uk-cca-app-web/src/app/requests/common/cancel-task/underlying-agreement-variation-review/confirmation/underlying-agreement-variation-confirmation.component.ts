import { ChangeDetectionStrategy, Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-variation-cancel-task-confirmation',
  template: `<netz-cancel-confirmation />`,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementVariationConfirmationComponent {}
