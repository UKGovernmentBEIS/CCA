import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';

import { PageHeadingComponent } from '../page-heading';

@Component({
  selector: 'netz-cancel-task',
  template: `
    <div class="govuk-grid-row">
      <netz-page-heading size="xl">{{ heading() }}</netz-page-heading>
      <ng-content />
      <div class="govuk-button-group">
        <button type="button" netzPendingButton (click)="cancel()" govukWarnButton>Yes, cancel this task</button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PendingButtonDirective, PageHeadingComponent, ButtonDirective],
})
export class CancelComponent {
  heading = input('Are you sure you want to cancel this task?');
  cancelled = output();

  cancel(): void {
    this.cancelled.emit();
  }
}
