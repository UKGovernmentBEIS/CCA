import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { performanceDataDownloadQuery } from '../+state/performance-data-download.selectors';

@Component({
  selector: 'cca-performance-data-download-confirmation',
  templateUrl: './performance-data-download-confirmation.component.html',
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataDownloadConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly zipFile = this.requestTaskStore.select(performanceDataDownloadQuery.selectZipFile);
}
