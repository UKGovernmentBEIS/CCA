import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-variation-cancel-task',
  template: `
    <govuk-warning-text>You will not be able to undo this action.</govuk-warning-text>
    <div class="govuk-hint">Your task and all its data will be deleted permanently.</div>
  `,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationCancelTaskComponent {}
