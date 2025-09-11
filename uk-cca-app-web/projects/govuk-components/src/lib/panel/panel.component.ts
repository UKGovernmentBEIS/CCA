import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-panel',
  templateUrl: './panel.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PanelComponent {
  readonly title = input<string>();
}
