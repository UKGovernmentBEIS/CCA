import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { toTargetUnitCreateSubmittedSummaryData } from '@shared/utils';

import { targetUnitCreationTimelineQuery } from './+state/target-unit-account-submitted-timeline.selectors';

@Component({
  selector: 'cca-target-unit-account-submitted-timeline',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitAccountSubmittedTimelineComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(targetUnitCreationTimelineQuery.selectPayload)();

  protected readonly data = toTargetUnitCreateSubmittedSummaryData(this.actionPayload.payload);
}
