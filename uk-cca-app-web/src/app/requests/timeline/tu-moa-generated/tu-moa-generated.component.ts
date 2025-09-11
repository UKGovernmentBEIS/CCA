import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { TargetUnitMoaGeneratedRequestActionPayload } from 'cca-api';

import { toTargetUnitMoaGeneratedSummaryData } from './tu-moa-generated-summary';

@Component({
  selector: 'cca-tu-moa-generated',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuMoaGeneratedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as TargetUnitMoaGeneratedRequestActionPayload;

  protected readonly data = toTargetUnitMoaGeneratedSummaryData(this.actionPayload);
}
