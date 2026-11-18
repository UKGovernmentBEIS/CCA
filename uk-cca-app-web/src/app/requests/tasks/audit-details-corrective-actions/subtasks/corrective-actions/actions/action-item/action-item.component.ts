import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { DateInputComponent, TextareaComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';

import { CorrectiveActionFormModel } from '../corrective-actions-form.provider';

@Component({
  selector: 'cca-action-item',
  templateUrl: './action-item.component.html',
  imports: [ReactiveFormsModule, TextareaComponent, DateInputComponent],
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActionItemComponent {
  protected readonly group = input.required<CorrectiveActionFormModel>();
  protected readonly index = input.required<number>();
  protected readonly canRemove = input<boolean>(false);

  readonly remove = output<number>();

  protected onRemove() {
    this.remove.emit(this.index());
  }
}
