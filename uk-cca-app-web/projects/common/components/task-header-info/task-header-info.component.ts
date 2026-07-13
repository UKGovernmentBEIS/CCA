import { DatePipe, TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { DaysRemainingPipe } from '@netz/common/pipes';

@Component({
  selector: 'netz-task-header-info',
  template: `
    <div class="govuk-!-margin-top-2">
      <p class="govuk-body govuk-!-margin-bottom-1"><strong>Assigned to:</strong> {{ assignee() }}</p>
    </div>

    @if (daysRemaining() !== undefined && daysRemaining() !== null) {
      <p class="govuk-body govuk-!-margin-bottom-1">
        <strong>Days Remaining:</strong> {{ daysRemaining() | daysRemaining }}
      </p>
    }

    @if (isTPRTask() && startDate() !== undefined && startDate() !== null) {
      <p class="govuk-body govuk-!-margin-bottom-1">
        <strong>Date initiated:</strong> {{ startDate() | date: 'dd/MM/yyyy' }}
      </p>
    }

    @if (payload()?.targetPeriodType) {
      <p class="govuk-body govuk-!-margin-bottom-1">
        <strong>Target period:</strong> {{ payload()?.targetPeriodType }}
      </p>
    }

    @if (payload()?.reportType) {
      <p class="govuk-body"><strong>Target period report type:</strong> {{ payload()?.reportType | titlecase }}</p>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DaysRemainingPipe, DatePipe, TitleCasePipe],
})
export class TaskHeaderInfoComponent {
  protected readonly assignee = input<string>();
  protected readonly daysRemaining = input<number>();
  protected readonly startDate = input<string>();
  protected readonly requestType = input<string>();
  protected readonly payload = input<any>();

  protected readonly isTPRTask = computed(() => this.requestType() === 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM');
}
