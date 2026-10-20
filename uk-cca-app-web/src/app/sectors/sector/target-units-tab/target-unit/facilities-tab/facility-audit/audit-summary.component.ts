import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { WarningTextComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { FacilityAuditControllerService, RequestsService } from 'cca-api';

import { toFacilityAuditSummaryData } from './audit-summary-data';

@Component({
  selector: 'cca-audit-summary',
  template: `
    <div class="govuk-!-width-two-thirds">
      @if (inProgressFacilityAuditTask()) {
        <govuk-warning-text assistiveText="">
          An audit task is in progress. You cannot start a new one until the previous audit task has been completed.
        </govuk-warning-text>
      }
    </div>

    <cca-summary [data]="data()" />
  `,
  imports: [SummaryComponent, WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityAuditControllerService = inject(FacilityAuditControllerService);
  private readonly requestsService = inject(RequestsService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly availableWorkflows = toSignal(
    this.requestsService.getAvailableWorkflows('FACILITY', this.facilityId),
  );

  private readonly audit = toSignal(
    this.facilityAuditControllerService.getFacilityAuditViewByFacilityId(this.facilityId),
  );

  protected readonly data = computed(() => toFacilityAuditSummaryData(this.audit()));

  protected readonly inProgressFacilityAuditTask = computed(
    () =>
      !this.availableWorkflows()?.['FACILITY_AUDIT']?.valid &&
      this.availableWorkflows()?.['FACILITY_AUDIT']?.requests.includes('FACILITY_AUDIT'),
  );
}
