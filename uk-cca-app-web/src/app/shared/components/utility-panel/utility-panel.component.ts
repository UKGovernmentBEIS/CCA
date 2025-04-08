import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'cca-utility-panel',
  templateUrl: './utility-panel.component.html',
  styleUrls: ['./utility-panel.component.scss'],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UtilityPanelComponent {
  readonly heading = input<string>();
}
