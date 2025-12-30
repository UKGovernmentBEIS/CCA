import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GovukDatePipe, ItemActionHeaderPipe } from '@netz/common/pipes';

import { RequestActionInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-timeline-item',
  template: `
    <h3 class="govuk-heading-s govuk-!-margin-bottom-1">{{ action() | itemActionHeader }}</h3>
    <p class="govuk-!-margin-bottom-1">{{ action().creationDate | govukDate: 'datetime' }}</p>

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

  protected readonly action = input<RequestActionInfoDTO>(undefined);
  protected readonly link = input<any[]>(undefined);
  protected readonly state = input<any>(undefined);
}
