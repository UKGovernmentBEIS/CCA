import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { performanceDataUploadQuery } from '../+state/performance-data-upload-selectors';

@Component({
  selector: 'cca-performance-data-upload-confirmation',
  standalone: true,
  imports: [PanelComponent, RouterLink],
  templateUrl: 'performance-data-upload-confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  readonly errorMessage = this.requestTaskStore.select(performanceDataUploadQuery.selectErrorMessage);
}
