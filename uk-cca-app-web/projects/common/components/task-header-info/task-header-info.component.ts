import { DatePipe, TitleCasePipe } from '@angular/common';
import { Component, computed, input } from '@angular/core';

import { DaysRemainingPipe } from '@netz/common/pipes';

import { RequestTaskPayload } from 'cca-api';

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

    @if ((isTPRTask() || isEtsTask()) && startDate() !== undefined && startDate() !== null) {
      <p class="govuk-body govuk-!-margin-bottom-1">
        <strong>Date initiated:</strong> {{ startDate() | date: 'dd/MM/yyyy' }}
      </p>
    }

    @if (isEtsTask()) {
      <p class="govuk-body govuk-!-margin-bottom-1"><strong>Year:</strong> {{ payload()?.targetYear }}</p>
      <p class="govuk-body govuk-!-margin-bottom-1">
        <strong>Participates in UK ETS:</strong> {{ payload()?.uketsId ? 'Yes, ' + payload()?.uketsId : 'No' }}
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
  imports: [DaysRemainingPipe, DatePipe, TitleCasePipe],
})
export class TaskHeaderInfoComponent {
  protected readonly assignee = input<string>();
  protected readonly daysRemaining = input<number>();
  protected readonly startDate = input<string>();
  protected readonly requestType = input<string>();
  protected readonly payload = input<
    RequestTaskPayload & { targetPeriodType?: string; reportType?: string; targetYear?: number; uketsId?: string }
  >();

  protected readonly isTPRTask = computed(() => this.requestType() === 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM');
  protected readonly isEtsTask = computed(() => this.requestType() === 'EMISSIONS_TRADING_SCHEME_DATA');
}
