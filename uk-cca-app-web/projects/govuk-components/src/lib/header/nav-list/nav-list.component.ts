import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-header-nav-list',
  templateUrl: './nav-list.component.html',
  styleUrl: './nav-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderNavListComponent {
  readonly identifier = input('navigation');
  readonly ariaLabel = input('Navigation menu');
  readonly menuButtonAriaLabel = input('Show or hide navigation menu');

  protected isNavigationOpen = false;
}
