import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import {
  PerformanceDataDownloadPayload,
  PerformanceDataTargetPeriodEnum,
  UploadProcessingComponent,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';
import { fileUtils } from '@shared/utils';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { PATUploadQuery } from './+state/pat-selectors';
import { PATUploadPayload } from './pat.types';
import {
  PATUploadProcessFormProvider,
  UPLOAD_PROCESS_PAT_FORM,
  UploadProcessPATFormModel,
} from './pat-upload-process-form.provider';
import { PatUploadProcessedComponent } from './processed/pat-upload-processed.component';

@Component({
  selector: 'cca-performance-account-template-upload',
  templateUrl: './pat-upload-process.component.html',
  imports: [
    ReactiveFormsModule,
    SelectComponent,
    DetailsComponent,
    MultipleFileInputComponent,
    RouterLink,
    UploadProcessingComponent,
    PatUploadProcessedComponent,
    WizardStepComponent,
  ],
  providers: [PATUploadProcessFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PATUploadProcessComponent implements OnInit {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly form = inject<UploadProcessPATFormModel>(UPLOAD_PROCESS_PAT_FORM);

  private readonly taskId = +this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly interval = 10000;

  protected readonly targetPeriodsOptions: GovukSelectOption<PerformanceDataTargetPeriodEnum.TP6>[] = [
    {
      value: PerformanceDataTargetPeriodEnum.TP6,
      text: PerformanceDataTargetPeriodEnum.TP6,
    },
  ];

  protected readonly processingStatus = this.requestTaskStore.select(PATUploadQuery.selectProcessingStatus);

  ngOnInit() {
    if (this.processingStatus() === 'IN_PROGRESS') this.fetchTaskItemInfo().subscribe();
  }

  onSubmit() {
    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING_PAYLOAD',
          ...{
            targetPeriodType: this.form.value.targetPeriodType,
            reportPackages: fileUtils.toUUIDs(this.form.value.uploadedFiles),
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
      switchMap((payload: PATUploadPayload) => {
        if (payload.processingStatus === 'IN_PROGRESS') return this.fetchTaskItemInfo();
        this.requestTaskStore.setPayload(payload);
        return EMPTY;
      }),
    );
  }
}
