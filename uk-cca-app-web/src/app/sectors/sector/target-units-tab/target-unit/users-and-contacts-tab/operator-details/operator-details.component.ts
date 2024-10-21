import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { LinkDirective } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { PageHeadingComponent } from '@shared/components';

import { ActiveOperatorStore } from './active-operator.store';
import { toSummaryData } from './operator-details-summary-helper';

@Component({
  selector: 'cca-operator-details',
  templateUrl: './operator-details.component.html',
  standalone: true,
  imports: [LinkDirective, RouterLink, PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(ActiveOperatorStore);
  private readonly currentUserId = inject(AuthStore).select(selectUserId);
  private readonly operatorId = this.route.snapshot.paramMap.get('userId');
  private readonly isCurrentUser = this.currentUserId() === this.operatorId;

  private readonly changeContactType = this.store.state.editable || this.isCurrentUser;
  readonly operatorUserDetails = this.store.state.details;
  readonly summaryData = toSummaryData(this.operatorUserDetails, this.changeContactType);
}
