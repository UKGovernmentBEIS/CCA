import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { adminTerminationQuery } from '../../admin-termination.selectors';

@Component({
  selector: 'cca-confirmation',
  templateUrl: './confirmation.component.html',
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly reasonDetails = this.requestTaskStore.select(adminTerminationQuery.selectReasonDetails);

  protected readonly isRegulatoryReasonSelected = computed(
    () =>
      !!this.reasonDetails() &&
      ['FAILURE_TO_COMPLY', 'FAILURE_TO_AGREE', 'FAILURE_TO_PAY'].includes(this.reasonDetails().reason),
  );
}
