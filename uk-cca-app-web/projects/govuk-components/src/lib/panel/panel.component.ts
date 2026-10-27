import { Component, input } from '@angular/core';

@Component({
  selector: 'govuk-panel',
  templateUrl: './panel.component.html',
})
export class PanelComponent {
  readonly title = input<string>();
}
