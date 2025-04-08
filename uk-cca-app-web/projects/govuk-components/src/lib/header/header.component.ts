import { ChangeDetectionStrategy, Component, ElementRef, Input, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  @Input() title: string;
  @ViewChild('header') header: ElementRef;
}
