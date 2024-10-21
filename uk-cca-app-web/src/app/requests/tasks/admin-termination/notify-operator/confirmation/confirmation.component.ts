import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { LinkDirective, PanelComponent } from '@netz/govuk-components';

import { AdminTerminationQuery } from '../../+state/admin-termination.selectors';

@Component({
  selector: 'cca-confirmation',
  templateUrl: './confirmation.component.html',
  standalone: true,
  imports: [PanelComponent, LinkDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly adminTerminationReasonDetails = this.requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationReasonDetails,
  )();

  protected readonly isRegulatoryReasonSelected =
    !!this.adminTerminationReasonDetails &&
    (this.adminTerminationReasonDetails.reason === 'FAILURE_TO_COMPLY' ||
      this.adminTerminationReasonDetails.reason === 'FAILURE_TO_AGREE' ||
      this.adminTerminationReasonDetails.reason === 'FAILURE_TO_PAY');
}
