import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { SectorMoaGeneratedRequestActionPayload } from 'cca-api';

import { toSectorMoaGeneratedSummaryData } from './sector-moa-generated-summary';

@Component({
  selector: 'cca-sector-moa-generated',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorMoaGeneratedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as SectorMoaGeneratedRequestActionPayload;

  protected readonly data = toSectorMoaGeneratedSummaryData(this.actionPayload);
}
