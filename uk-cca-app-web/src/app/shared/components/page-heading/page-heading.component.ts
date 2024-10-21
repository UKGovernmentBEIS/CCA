import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'cca-page-heading',
  standalone: true,
  template: `
    @if (caption) {
      <span [class]="'govuk-caption-' + size">{{ caption }}</span>
    }

    <h1 [class]="'govuk-heading-' + size">
      <ng-content></ng-content>
    </h1>
  `,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageHeadingComponent {
  @Input() caption: string;
  @Input() size: 'l' | 'xl' = 'l';
}
