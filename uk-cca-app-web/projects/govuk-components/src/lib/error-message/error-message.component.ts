import { KeyValuePipe } from '@angular/common';
import { Component, computed, model } from '@angular/core';

import { displayValidationErrors } from './display-validation-errors';
import { MessageValidationErrors } from './message-validation-errors';

@Component({
  selector: 'govuk-error-message',
  templateUrl: './error-message.component.html',
  imports: [KeyValuePipe],
})
export class ErrorMessageComponent {
  identifier = model<string>();
  errors = model<MessageValidationErrors>();
  protected readonly displayedErrors = computed(() => displayValidationErrors(this.errors()));
}
