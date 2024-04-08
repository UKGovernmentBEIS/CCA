import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';

@Component({
  selector: 'cca-request-action-heading',
  template: `
    <cca-page-heading>{{ headerText }}</cca-page-heading>
    <p class="govuk-caption-m">{{ timelineCreationDate | govukDate: 'datetime' }}</p>
    <ng-content></ng-content>
  `,
  standalone: true,
  imports: [PageHeadingComponent, GovukDatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestActionHeadingComponent {
  @Input() headerText: string;
  @Input() timelineCreationDate: string;
}
