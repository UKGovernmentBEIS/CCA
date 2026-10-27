import { Component } from '@angular/core';

import { CancelConfirmationComponent } from '@netz/common/components';

@Component({
  selector: 'cca-underlying-agreement-variation-cancel-task-confirmation',
  template: `<netz-cancel-confirmation />`,
  imports: [CancelConfirmationComponent],
})
export class UnderlyingAgreementVariationConfirmationComponent {}
