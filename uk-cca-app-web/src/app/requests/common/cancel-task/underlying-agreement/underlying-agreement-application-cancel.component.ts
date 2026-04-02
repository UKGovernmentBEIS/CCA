import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-cancel-task',
  template: `
    <govuk-warning-text>You will not be able to undo this action.</govuk-warning-text>
    <div class="govuk-hint">
      This workflow will be terminated and none of its data will be available for further processing. In addition, the
      target unit account will be closed.
    </div>
  `,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementCancelTaskComponent {}
