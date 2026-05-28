import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, of } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  calculateThroughputValues,
  decideVariableEnergyType,
  TaskItemStatus,
  TasksApiService,
  ThroughputDetailsSummaryComponent,
  toTotalsOnlySummaryData,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { produce } from 'immer';

import { tprFormQuery } from '../../../target-period-reporting-form.selectors';
import { createRequestTaskActionProcessDTO, toPerformanceDataFacilityDigitalFormSavePayload } from '../../../transform';

@Component({
  selector: 'cca-tpr-throughput-details-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Provide target period throughput details">Check your answers</netz-page-heading>

      @if (variableEnergyType() === 'BY_PRODUCT') {
        <cca-throughput-details-summary
          [referenceData]="referenceData()"
          [performanceData]="performanceData()"
          [isEditable]="isEditable()"
          [reportType]="reportType()"
          [targetPeriodType]="targetPeriodType()"
        />
      } @else {
        <cca-summary [data]="summaryData()" />
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
    SummaryComponent,
    ThroughputDetailsSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputDetailsCheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);
  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);
  protected readonly targetPeriodYear = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodYear);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly calculations = computed(() =>
    calculateThroughputValues({
      referenceData: this.referenceData(),
      performanceData: this.performanceData(),
      reportType: this.reportType(),
      targetPeriodType: this.targetPeriodType(),
      actualThroughput: this.performanceData().throughputDetails?.actualThroughput ?? null,
    }),
  );

  protected readonly variableEnergyType = computed(() =>
    decideVariableEnergyType(this.referenceData()?.baselineAndTargets?.variableEnergyType),
  );

  protected readonly summaryData = computed(() =>
    toTotalsOnlySummaryData(
      this.referenceData(),
      this.performanceData(),
      this.calculations().targetVariableEnergy,
      this.isEditable(),
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);

    const currentSectionsCompleted = this.requestTaskStore.select(tprFormQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
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
