import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { AdminTerminationCancelTaskConfirmationComponent } from './admin-termination/confirmation/admin-termination-cancel-confirmation.component';
import { UnderlyingAgreementCancelTaskConfirmationComponent } from './underlying-agreement/confirmation/underlying-agreement-cancel-task-confirmation.component';
import { UnderlyingAgreementVariationSubmitConfirmationComponent } from './underlying-agreement-variation/confirmation/underlying-agreement-confirmation.component';
import { UnderlyingAgreementVariationConfirmationComponent } from './underlying-agreement-variation-review/confirmation/underlying-agreement-variation-confirmation.component';

@Component({
  selector: 'cca-cancel-task',
  standalone: true,
  imports: [
    UnderlyingAgreementCancelTaskConfirmationComponent,
    AdminTerminationCancelTaskConfirmationComponent,
    UnderlyingAgreementVariationSubmitConfirmationComponent,
    UnderlyingAgreementVariationConfirmationComponent,
  ],
  templateUrl: './cancel-confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelTaskConfirmationComponent {
  private readonly store = inject(RequestTaskStore);
  requestTaskType = this.store.select(requestTaskQuery.selectRequestTaskType)();
}
