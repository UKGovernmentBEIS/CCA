import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  GovukSelectOption,
  SelectComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { PerformanceDataDownloadPayload, PerformanceDataTargetPeriodEnum } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { performanceDataDownloadQuery } from '../+state/performance-data-download.selectors';
import { PerformanceDataDownloadGeneratedComponent } from './generated/performance-data-download-generated.component';
import { PerformanceDataDownloadGeneratingComponent } from './generating/performance-data-download-generating.component';
import {
  GENERATE_PERFORMANCE_DATA_FORM,
  GeneratePerformanceDataFormModel,
  PerformanceDataDownloadGenerateFormProvider,
} from './performance-data-download-generate-form.provider';

@Component({
  selector: 'cca-performance-data-download-generate',
  templateUrl: './performance-data-download-generate.component.html',
  standalone: true,
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SelectComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    PerformanceDataDownloadGeneratingComponent,
    PerformanceDataDownloadGeneratedComponent,
  ],
  providers: [PerformanceDataDownloadGenerateFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataDownloadGenerateComponent implements OnInit {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly form = inject<GeneratePerformanceDataFormModel>(GENERATE_PERFORMANCE_DATA_FORM);

  private readonly taskId = +this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly interval = 10000; // ms

  protected readonly targetPeriodsOptions: GovukSelectOption<PerformanceDataTargetPeriodEnum.TP6>[] = [
    {
      value: PerformanceDataTargetPeriodEnum.TP6,
      text: PerformanceDataTargetPeriodEnum.TP6,
    },
  ];

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly sectorAssociationInfo = this.requestTaskStore.select(
    performanceDataDownloadQuery.selectSectorAssociationInfo,
  );

  protected readonly targetPeriodType = this.requestTaskStore.select(
    performanceDataDownloadQuery.selectTargetPeriodType,
  );

  protected readonly processCompleted = this.requestTaskStore.select(
    performanceDataDownloadQuery.selectProcessCompleted,
  );

  ngOnInit() {
    if (this.processCompleted() === false) this.fetchTaskItemInfo().subscribe();
  }

  onSubmit() {
    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_DOWNLOAD_GENERATE',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'PERFORMANCE_DATA_DOWNLOAD_GENERATE_PAYLOAD',
          ...this.form.value,
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe((response: PerformanceDataDownloadPayload) => {
        this.requestTaskStore.setPayload(response);
        this.fetchTaskItemInfo().subscribe();
      });
  }

  private fetchTaskItemInfo(): Observable<unknown> {
    return timer(this.interval).pipe(
      take(1),
      switchMap(() => this.tasksService.getTaskItemInfoById(this.taskId)),
      takeUntilDestroyed(this.destroyRef),
      map((r) => r.requestTask.payload),
      switchMap((payload: PerformanceDataDownloadPayload) => {
        if (payload.processCompleted === false) return this.fetchTaskItemInfo();
        this.requestTaskStore.setPayload(payload);
        return EMPTY;
      }),
    );
  }
}
