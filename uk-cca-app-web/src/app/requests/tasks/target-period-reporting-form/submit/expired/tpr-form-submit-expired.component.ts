import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';
import { tprFormQuery } from '@requests/common';

@Component({
  selector: 'cca-tpr-form-submit-expired',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Target period report expired" />
        <p>The task to submit the {{ reportName() }} for this facility has now expired.</p>
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
})
export class TprFormSubmitExpiredComponent {
  private readonly reportType = inject(RequestTaskStore).select(tprFormQuery.selectReportType);

  protected readonly reportName = computed(() =>
    this.reportType() === 'INTERIM' ? 'interim target period report' : 'target period report',
  );
}
