import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-phase-banner',
  standalone: true,
  imports: [NgIf],
  templateUrl: './phase-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./phase-banner.component.scss'],
})
export class PhaseBannerComponent {
  @Input() phase: string;
  @Input() tagColor: string;
  @Input() tagAlign: 'right' | 'left' = 'left';
}
