import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'cca-related-content',
  standalone: true,
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">{{ header }}</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ng-content></ng-content>
      </nav>
    </aside>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedContentComponent {
  @Input() header = 'Related content';
}
