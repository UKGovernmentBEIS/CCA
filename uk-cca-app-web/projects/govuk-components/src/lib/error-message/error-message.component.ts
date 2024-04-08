import { KeyValuePipe, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { MessageValidationErrors } from './message-validation-errors';

@Component({
  selector: 'govuk-error-message',
  standalone: true,
  imports: [KeyValuePipe, NgForOf],
  templateUrl: './error-message.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorMessageComponent {
  @Input() identifier: string;
  @Input() errors: MessageValidationErrors;
}
