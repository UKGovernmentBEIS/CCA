import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { preAuditReviewQuery } from '../../pre-audit-review.selectors';

@Component({
  selector: 'cca-pre-audit-review-confirmation',
  template: `
    <govuk-panel title="Pre-audit review complete">
      Outcome <br />
      <span style="font-weight: bold;">{{ outcome() }}</span>
    </govuk-panel>

    @if (furtherAuditNeeded()) {
      <h3 class="govuk-heading-m">What happens next</h3>
      <p>A new task, 'Audit details and corrective actions', has been assigned to you.</p>
    }

    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly preAuditReviewDetails = this.requestTaskStore.select(
    preAuditReviewQuery.selectPreAuditReviewDetails,
  );

  protected readonly furtherAuditNeeded = computed(
    () => this.preAuditReviewDetails()?.auditDetermination?.furtherAuditNeeded,
  );

  protected readonly outcome = computed(() =>
    this.furtherAuditNeeded() ? 'Further audit needed' : 'No further audit needed',
  );
}
