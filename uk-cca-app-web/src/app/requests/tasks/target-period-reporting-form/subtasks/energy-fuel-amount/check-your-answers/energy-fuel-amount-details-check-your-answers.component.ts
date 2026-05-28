import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, of } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  buildEnergyFuelRows,
  EnergyFuelAmountSummaryComponent,
  MeasurementTypeToUnitEnum,
  TaskItemStatus,
  TasksApiService,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import { tprFormQuery } from '../../../target-period-reporting-form.selectors';
import { createRequestTaskActionProcessDTO, toPerformanceDataFacilityDigitalFormSavePayload } from '../../../transform';

@Component({
  selector: 'cca-energy-fuel-amount-details-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Provide energy/fuel amount consumed">Check your answers</netz-page-heading>

      <cca-energy-fuel-amount-summary
        [energyFuelDetails]="energyFuelDetails()"
        [isEditable]="isEditable()"
        [measurementType]="measurementType()"
        [usedReportingMechanism]="usedReportingMechanism()"
      />

      @if (showNoConsumptionMessage()) {
        <p>
          By selecting "Confirm and complete" you declare that the facility has not used any electricity and that the
          emissions from the combustion of all other fuels types are reported under UK ETS
        </p>
      }

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    EnergyFuelAmountSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnergyFuelAmountDetailsCheckYourAnswersComponent {
  protected readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);

  protected readonly energyFuelDetails = computed(
    () => this.requestTaskStore.select(tprFormQuery.selectPerformanceData)()?.energyFuelDetails,
  );
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly measurementType = computed(() => {
    const measurementType = this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets
      ?.measurementType;
    return measurementType ? MeasurementTypeToUnitEnum[measurementType] : 'kWh';
  });

  protected readonly usedReportingMechanism = computed(
    () => this.requestTaskStore.select(tprFormQuery.selectReferenceData)()?.baselineAndTargets?.usedReportingMechanism,
  );

  protected readonly showNoConsumptionMessage = computed(
    () => buildEnergyFuelRows(this.energyFuelDetails()).length === 0,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);

    const currentSectionsCompleted = this.requestTaskStore.select(tprFormQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, actionPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((error) => {
          console.error(error);
          return of(null);
        }),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true }));
  }
}
