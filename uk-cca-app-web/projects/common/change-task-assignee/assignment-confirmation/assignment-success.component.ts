import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'netz-assignment-success',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        @if (user(); as user) {
          <govuk-panel title="The task has been reassigned to">{{ user }}</govuk-panel>
          <h3 class="govuk-heading-m">What happens next</h3>
          <p>The task will appear in the dashboard of the person it has been assigned to</p>
        } @else {
          <govuk-panel title="This task has been unassigned" />
          <h3 class="govuk-heading-m">What happens next</h3>
          <p>The task will appear in the unassigned tab of your dashboard</p>
        }

        <a class="govuk-link" routerLink="/dashboard">Return to dashboard</a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignmentSuccessComponent {
  private readonly store = inject(RequestTaskStore);

  protected user = this.store.select(requestTaskQuery.selectTaskReassignedTo);
}
