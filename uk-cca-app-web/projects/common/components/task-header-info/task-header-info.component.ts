import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'netz-task-header-info',
  standalone: true,
  template: `
    <div class="govuk-!-margin-top-2">
      <p class="govuk-body"><strong>Assigned to:</strong> {{ assignee }}</p>
    </div>
    <ng-container *ngIf="daysRemaining !== undefined && daysRemaining !== null">
      <div class="govuk-!-margin-top-2">
        <p class="govuk-body"><strong>Days Remaining:</strong> {{ daysRemaining }}</p>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgIf],
})
export class TaskHeaderInfoComponent {
  @Input() assignee: string;
  @Input() daysRemaining: number;
}
