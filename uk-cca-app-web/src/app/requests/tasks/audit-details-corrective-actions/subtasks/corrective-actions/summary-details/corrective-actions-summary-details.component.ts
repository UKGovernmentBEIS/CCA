import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { CorrectiveActions } from 'cca-api';

@Component({
  selector: 'cca-corrective-actions-summary-details',
  templateUrl: './corrective-actions-summary-details.component.html',
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    RouterLink,
    DatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CorrectiveActionsSummaryDetailsComponent {
  readonly correctiveActions = input.required<CorrectiveActions>();
  readonly isEditable = input.required<boolean>();

  readonly remove = output<number>();
  readonly add = output<void>();

  onRemoveAction(index: number) {
    this.remove.emit(index);
  }

  onAddAction() {
    this.add.emit();
  }
}
