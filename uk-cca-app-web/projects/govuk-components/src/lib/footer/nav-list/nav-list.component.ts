import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-footer-nav-list',
  standalone: true,
  imports: [NgIf],
  templateUrl: './nav-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterNavListComponent {
  @Input() title: string;
  @Input() columns: 1 | 2;
}
