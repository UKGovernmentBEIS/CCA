import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'netz-page-heading',
  template: `
    @if (caption()) {
      <span [class]="'govuk-caption-' + size()">{{ caption() }}</span>
    }

    <h1 [class]="'govuk-heading-' + size()">
      <ng-content />
    </h1>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageHeadingComponent {
  protected readonly caption = input<string>(undefined);
  protected readonly size = input<'l' | 'xl'>('l');
}
