import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-warning-text',
  standalone: true,
  templateUrl: './warning-text.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WarningTextComponent {
  readonly assistiveText = input('Warning');
}
