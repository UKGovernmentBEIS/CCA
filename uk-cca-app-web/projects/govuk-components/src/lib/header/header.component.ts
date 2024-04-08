import { ChangeDetectionStrategy, Component, ElementRef, Input, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

import { HeaderActionsListComponent } from './actions-list/actions-list.component';

@Component({
  selector: 'govuk-header',
  standalone: true,
  imports: [RouterLink, HeaderActionsListComponent],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  @Input() title: string;
  @ViewChild('header') header: ElementRef;
}
