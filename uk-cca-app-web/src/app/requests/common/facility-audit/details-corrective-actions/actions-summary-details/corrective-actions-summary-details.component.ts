import { afterRenderEffect, Component, ElementRef, input, output, viewChild, viewChildren } from '@angular/core';
import { RouterLink } from '@angular/router';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { CalendarDatePipe } from '@shared/pipes';

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
    CalendarDatePipe,
  ],
})
export class CorrectiveActionsSummaryDetailsComponent {
  readonly correctiveActions = input.required<CorrectiveActions>();
  readonly isEditable = input.required<boolean>();

  readonly remove = output<number>();
  readonly add = output<void>();

  private readonly changeActionLinks = viewChildren('changeActionLink', { read: ElementRef });
  private readonly addActionButton = viewChild('addActionButton', { read: ElementRef });

  private removedIndex: number | null = null;

  constructor() {
    // The parent removes the action through the API, so focus moves once the shorter list has rendered.
    afterRenderEffect({
      write: () => {
        const links = this.changeActionLinks();
        const addButton = this.addActionButton();
        if (this.removedIndex === null) return;

        (links[this.removedIndex]?.nativeElement ?? addButton?.nativeElement)?.focus();
        this.removedIndex = null;
      },
    });
  }

  onRemoveAction(index: number) {
    this.removedIndex = index;
    this.remove.emit(index);
  }

  onAddAction() {
    this.add.emit();
  }
}
