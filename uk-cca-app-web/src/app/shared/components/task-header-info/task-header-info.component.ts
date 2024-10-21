import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'cca-task-header-info',
  standalone: true,
  template: `
    <div class="govuk-!-margin-top-2">
      <p class="govuk-body"><strong>Assigned to:</strong> {{ assignee }}</p>
    </div>

    @if (daysRemaining !== undefined && daysRemaining !== null) {
      <div class="govuk-!-margin-top-2">
        <p class="govuk-body"><strong>Days Remaining:</strong> {{ daysRemaining }}</p>
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
})
export class TaskHeaderInfoComponent {
  @Input() assignee: string;
  @Input() daysRemaining: number;
}
