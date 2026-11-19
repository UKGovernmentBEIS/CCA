import { Component, input } from '@angular/core';

@Component({
  selector: 'govuk-warning-text',
  templateUrl: './warning-text.component.html',
})
export class WarningTextComponent {
  readonly assistiveText = input('Warning');
}
