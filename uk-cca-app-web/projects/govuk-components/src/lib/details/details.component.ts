import { Component, input } from '@angular/core';

@Component({
  selector: 'govuk-details',
  templateUrl: './details.component.html',
})
export class DetailsComponent {
  readonly summary = input<string>();
}
