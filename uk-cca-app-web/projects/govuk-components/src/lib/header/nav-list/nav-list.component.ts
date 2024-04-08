import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-header-nav-list',
  standalone: true,
  templateUrl: './nav-list.component.html',
  styleUrls: ['./nav-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderNavListComponent {
  @Input() identifier = 'navigation';
  @Input() ariaLabel = 'Navigation menu';
  @Input() menuButtonAriaLabel = 'Show or hide navigation menu';

  isNavigationOpen = false;
}
