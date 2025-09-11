import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-admin-termination-cancel-task',
  template: `
    <div class="govuk-warning-text">
      <span class="govuk-warning-text__icon" aria-hidden="true">!</span>
      <strong class="govuk-warning-text__text">
        <span class="govuk-visually-hidden">Warning</span>
        You will not be able to undo this action.
      </strong>
    </div>
    <div class="govuk-hint">Your task and all its data will be deleted permanently.</div>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminTerminationCancelTaskComponent {}
