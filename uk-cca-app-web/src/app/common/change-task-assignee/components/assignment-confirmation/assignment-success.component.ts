import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@common/request-task/+state';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'cca-assignment-success',
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <ng-container *ngIf="user() as user; else unassigned">
          <govuk-panel title="The task has been reassigned to">{{ user }}</govuk-panel>
          <h3 class="govuk-heading-m">What happens next</h3>
          <p class="govuk-body">The task will appear in the dashboard of the person it has been assigned to</p>
        </ng-container>
        <ng-template #unassigned>
          <govuk-panel title="This task has been unassigned"></govuk-panel>
          <h3 class="govuk-heading-m">What happens next</h3>
          <p class="govuk-body">The task will appear in the unassigned tab of your dashboard</p>
        </ng-template>
      </div>
    </div>
  `,
  imports: [GovukComponentsModule, NgIf, AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignmentSuccessComponent extends BaseSuccessComponent {
  protected user = this.store.select(requestTaskQuery.selectTaskReassignedTo);

  constructor(private readonly store: RequestTaskStore) {
    super();
  }
}
