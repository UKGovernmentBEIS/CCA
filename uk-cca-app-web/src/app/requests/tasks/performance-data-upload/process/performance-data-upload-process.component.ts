import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { PerformanceDataDownloadPayload, PerformanceDataTargetPeriodEnum } from '@requests/common';
import { UploadProcessingComponent } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';
import { fileUtils } from '@shared/utils';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { performanceDataUploadQuery } from '../+state/performance-data-upload-selectors';
import {
  PerformanceDataUploadProcessFormProvider,
  UPLOAD_PROCESS_PERFORMANCE_DATA_FORM,
  UploadProcessPerformanceDataFormModel,
} from './performance-data-upload-process-form.provider';
import { PerformanceDataUploadProcessedComponent } from './processed/performance-data-upload-processed.component';

@Component({
  selector: 'cca-performance-data-upload',
  templateUrl: './performance-data-upload-process.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    FormsModule,
    ReactiveFormsModule,
    SelectComponent,
    DetailsComponent,
    MultipleFileInputComponent,
    RouterLink,
    UploadProcessingComponent,
    PerformanceDataUploadProcessedComponent,
  ],
  providers: [PerformanceDataUploadProcessFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadProcessComponent implements OnInit {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly form = inject<UploadProcessPerformanceDataFormModel>(UPLOAD_PROCESS_PERFORMANCE_DATA_FORM);

  private readonly taskId = +this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly interval = 10000; // ms

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly performanceDataUpload = this.requestTaskStore.select(
    performanceDataUploadQuery.selectPerformanceDataUpload,
  );

  protected readonly processCompleted = this.requestTaskStore.select(performanceDataUploadQuery.selectProcessCompleted);

  protected readonly targetPeriodsOptions: GovukSelectOption<PerformanceDataTargetPeriodEnum.TP6>[] = [
    {
      value: PerformanceDataTargetPeriodEnum.TP6,
      text: PerformanceDataTargetPeriodEnum.TP6,
    },
  ];

  ngOnInit() {
    if (this.processCompleted() === false) this.fetchTaskItemInfo().subscribe();
  }

  onSubmit() {
    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_UPLOAD_PROCESSING',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'PERFORMANCE_DATA_UPLOAD_PROCESSING_PAYLOAD',
          ...{
            performanceDataUpload: {
              performanceDataTargetPeriodType: this.form.value.targetPeriodType,
              reportPackages: fileUtils.toUUIDs(this.form.value.uploadedFiles),
            },
          },
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
