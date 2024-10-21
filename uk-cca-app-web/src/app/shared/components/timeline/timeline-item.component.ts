import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GovukDatePipe, ItemActionHeaderPipe } from '@netz/common/pipes';
import { LinkDirective } from '@netz/govuk-components';

import { RequestActionInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-timeline-item',
  standalone: true,
  template: `
    <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ action | itemActionHeader }}</h3>
    <p class="govuk-body govuk-!-margin-bottom-1">{{ action.creationDate | govukDate: 'datetime' }}</p>

    @if (link) {
      <span><a [routerLink]="link" [relativeTo]="route" [state]="state" govukLink>View details</a></span>
    }

    <hr class="govuk-!-margin-top-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ItemActionHeaderPipe, GovukDatePipe, RouterLink, LinkDirective],
})
export class TimelineItemComponent {
  @Input() action: RequestActionInfoDTO;
  @Input() link: any[];
  @Input() state: any;

  constructor(protected readonly route: ActivatedRoute) {}
}
