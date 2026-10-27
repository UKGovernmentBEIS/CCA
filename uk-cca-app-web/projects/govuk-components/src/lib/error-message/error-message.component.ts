import { KeyValuePipe } from '@angular/common';
import { Component, model } from '@angular/core';

import { MessageValidationErrors } from './message-validation-errors';

@Component({
  selector: 'govuk-error-message',
  templateUrl: './error-message.component.html',
  imports: [KeyValuePipe],
})
export class ErrorMessageComponent {
  identifier = model<string>();
  errors = model<MessageValidationErrors>();
}
