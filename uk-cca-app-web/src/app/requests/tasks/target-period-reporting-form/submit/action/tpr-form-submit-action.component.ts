import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, DetailsComponent } from '@netz/govuk-components';
import {
  TasksApiService,
  toComparisonDataSummaryData,
  toResultsDetailsSummaryData,
  toTPRBaselineDataDetails,
  TP_REPORTING_SUBMIT_ERROR_MESSAGES,
  TpReportingSubmitErrorCode,
  tprFormQuery,
} from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo, SummaryComponent } from '@shared/components';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { createSubmitRequestTaskActionDTO } from '../../transform';

@Component({
  selector: 'cca-tpr-form-submit-action',
  templateUrl: './tpr-form-submit-action.component.html',
  imports: [
    ButtonDirective,
    DetailsComponent,
    ErrorSummaryComponent,
    PageHeadingComponent,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    SummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprFormSubmitActionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  readonly isErrorSummaryDisplayed = signal(false);
  readonly isCancelError = signal(false);
  readonly errorSummaryInfo = signal<ErrorSummaryInfo>({ message: '', link: '', linkText: '' });

  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);
  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);

  protected readonly isInterimReport = computed(() => this.reportType() === 'INTERIM');
  protected readonly baselineDetails = computed(() => toTPRBaselineDataDetails(this.referenceData(), '.'));

  protected readonly comparisonData = computed(() =>
    toComparisonDataSummaryData({
      targetPeriodType: this.targetPeriodType(),
      reportType: this.reportType(),
      referenceData: this.referenceData(),
      calculatedResults: this.performanceData()?.calculatedResults,
    }),
  );

  protected readonly resultsDetails = computed(() =>
    toResultsDetailsSummaryData({
      reportType: this.reportType(),
      calculatedResults: this.performanceData()?.calculatedResults,
    }),
  );

  onCancel() {
    this.router.navigate(['../../cancel'], { relativeTo: this.route, replaceUrl: true });
  }

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createSubmitRequestTaskActionDTO(requestTaskId);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((err) => {
          const info = TP_REPORTING_SUBMIT_ERROR_MESSAGES[err.error.code as TpReportingSubmitErrorCode];

          if (info) {
            this.errorSummaryInfo.set(info);
            this.isErrorSummaryDisplayed.set(true);
            this.isCancelError.set(['TPRDF1002', 'TPRDF1005'].includes(err.error.code));
          }

          return EMPTY;
        }),
      )
      .subscribe((payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) => {
        const path = payload?.expired ? 'expired' : 'confirmation';
        this.router.navigate([path], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
