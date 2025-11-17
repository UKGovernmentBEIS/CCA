import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-underlying-agreement-cancel-task',
  template: `
    <div class="govuk-warning-text">
      <span class="govuk-warning-text__icon" aria-hidden="true">!</span>
      <strong class="govuk-warning-text__text">
        <span class="govuk-visually-hidden">Warning</span>
        You will not be able to undo this action.
      </strong>
    </div>
    <div class="govuk-hint">
      This workflow will be terminated and none of its data will be available for further processing. In addition, the
      target unit account will be closed.
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementCancelTaskComponent {}
