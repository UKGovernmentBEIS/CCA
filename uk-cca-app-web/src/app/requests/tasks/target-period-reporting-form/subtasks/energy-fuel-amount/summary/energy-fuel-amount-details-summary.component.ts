import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { EnergyFuelAmountSummaryComponent, tprFormQuery } from '@requests/common';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit } from '@shared/pipes';

@Component({
  selector: 'cca-energy-fuel-amount-details-summary',
  template: `
    <div>
      <netz-page-heading caption="Provide energy/fuel amount consumed">Summary</netz-page-heading>

      <cca-energy-fuel-amount-summary
        [energyFuelDetails]="energyFuelDetails()"
        [isEditable]="isEditable()"
        [measurementUnit]="measurementUnit()"
        [usedReportingMechanism]="usedReportingMechanism()"
      />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, ReturnToTaskOrActionPageComponent, EnergyFuelAmountSummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnergyFuelAmountDetailsSummaryComponent {
  protected readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly energyFuelDetails = computed(
    () => this.requestTaskStore.select(tprFormQuery.selectPerformanceData)()?.energyFuelDetails,
  );
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly measurementUnit = computed<MeasurementUnit>(
    () =>
      MEASUREMENT_TYPE_TO_UNIT_MAP[
        this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets?.measurementType
      ],
  );

  protected readonly usedReportingMechanism = computed(
    () => this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets?.usedReportingMechanism,
  );
}
