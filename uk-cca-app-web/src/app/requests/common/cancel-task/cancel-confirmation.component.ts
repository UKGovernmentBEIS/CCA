import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { AdminTerminationCancelTaskConfirmationComponent } from './admin-termination/confirmation/admin-termination-cancel-confirmation.component';
import { UnderlyingAgreementCancelTaskConfirmationComponent } from './underlying-agreement/confirmation/underlying-agreement-cancel-task-confirmation.component';
import { UnderlyingAgreementVariationConfirmationComponent } from './underlying-agreement-variation/confirmation/underlying-agreement-confirmation.component';

@Component({
  selector: 'cca-cancel-task',
  standalone: true,
  imports: [
    UnderlyingAgreementCancelTaskConfirmationComponent,
    AdminTerminationCancelTaskConfirmationComponent,
    UnderlyingAgreementVariationConfirmationComponent,
  ],
  template: `
    @switch (requestTaskType) {
      @case ('UNDERLYING_AGREEMENT_APPLICATION_SUBMIT') {
        <cca-underlying-agreement-cancel-task-confirmation />
      }
      @case ('UNDERLYING_AGREEMENT_APPLICATION_REVIEW') {
        <cca-underlying-agreement-cancel-task-confirmation />
      }
      @case ('ADMIN_TERMINATION_APPLICATION_SUBMIT') {
        <cca-admin-termination-cancel-task-confirmation />
      }
      @case ('UNDERLYING_AGREEMENT_VARIATION_SUBMIT') {
        <cca-underlying-agreement-variation-cancel-task-confirmation />
      }
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelTaskConfirmationComponent {
  private readonly store = inject(RequestTaskStore);
  requestTaskType = this.store.select(requestTaskQuery.selectRequestTaskType)();
}
