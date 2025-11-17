import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { DaysRemainingPipe } from '@netz/common/pipes';

@Component({
  selector: 'netz-task-header-info',
  template: `
    <div class="govuk-!-margin-top-2">
      <p class="govuk-body"><strong>Assigned to:</strong> {{ assignee() }}</p>
    </div>

    @if (daysRemaining() !== undefined && daysRemaining() !== null) {
      <div class="govuk-!-margin-top-2">
        <p class="govuk-body"><strong>Days Remaining:</strong> {{ daysRemaining() | daysRemaining }}</p>
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DaysRemainingPipe],
})
export class TaskHeaderInfoComponent {
  readonly assignee = input<string>();
  readonly daysRemaining = input<number>();
}
