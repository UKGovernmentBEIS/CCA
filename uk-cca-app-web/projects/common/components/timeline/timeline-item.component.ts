import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GovukDatePipe, ItemActionHeaderPipe } from '@netz/common/pipes';

import { RequestActionInfoDTO } from 'cca-api';

@Component({
  selector: 'netz-timeline-item',
  template: `
    @if (action(); as item) {
      <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ item | itemActionHeader }}</h3>
      <p class="govuk-!-margin-bottom-1">{{ item.creationDate | govukDate: 'datetime' }}</p>
    }

    @if (link()) {
      <span><a [routerLink]="link()" [relativeTo]="route" [state]="state()" class="govuk-link">View details</a></span>
    }
    <hr class="govuk-!-margin-top-6" />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ItemActionHeaderPipe, GovukDatePipe, RouterLink],
})
export class TimelineItemComponent {
  protected readonly route = inject(ActivatedRoute);

  protected readonly action = input<RequestActionInfoDTO | undefined>(undefined);
  protected readonly link = input<(string | number | undefined)[] | undefined>(undefined);
  protected readonly state = input<Record<string, unknown> | undefined>(undefined);
}
