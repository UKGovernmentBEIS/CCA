import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { AdminTerminationCancelTaskConfirmationComponent } from './admin-termination/confirmation/admin-termination-cancel-confirmation.component';
import { Cca3MigrationAccountActivationCancelConfirmationComponent } from './cca3-migration-account-activation-cancel-confirmation/cca3-migration-account-activation-cancel-confirmation.component';
import { UnderlyingAgreementCancelTaskConfirmationComponent } from './underlying-agreement/confirmation/underlying-agreement-cancel-task-confirmation.component';
import { UnderlyingAgreementVariationSubmitConfirmationComponent } from './underlying-agreement-variation/confirmation/underlying-agreement-confirmation.component';
import { UnderlyingAgreementVariationConfirmationComponent } from './underlying-agreement-variation-review/confirmation/underlying-agreement-variation-confirmation.component';

@Component({
  selector: 'cca-cancel-task',
  templateUrl: './cancel-confirmation.component.html',
  imports: [
    UnderlyingAgreementCancelTaskConfirmationComponent,
    AdminTerminationCancelTaskConfirmationComponent,
    UnderlyingAgreementVariationSubmitConfirmationComponent,
    UnderlyingAgreementVariationConfirmationComponent,
    Cca3MigrationAccountActivationCancelConfirmationComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelTaskConfirmationComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly requestTaskType = this.store.select(requestTaskQuery.selectRequestTaskType)();
}
