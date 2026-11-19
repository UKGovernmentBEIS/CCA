import { Component, input } from '@angular/core';

@Component({
  selector: 'govuk-phase-banner',
  templateUrl: './phase-banner.component.html',
  styleUrl: './phase-banner.component.scss',
})
export class PhaseBannerComponent {
  readonly phase = input<string>();
  readonly tagColor = input<string>();
  readonly tagAlign = input<'right' | 'left'>('left');
}
