import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit, Signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { LoadingSpinnerComponent, MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';
import { fileUtils, generateDownloadUrl } from '@shared/utils';

import {
  PerformanceDataFacilityDataUploadSubmitRequestTaskPayload,
  PerformanceDataReportingViewInfoService,
  PerformanceDataReportTypeDTO,
  RequestTaskActionPayload,
  TasksService,
} from 'cca-api';

import { tprCSVUploadQuery } from '../target-period-reporting-csv-upload.selectors';
import {
  PerformanceDataUploadProcessFormProvider,
  TPR_CSV_UPLOAD_PROCESS_FORM,
  TprCSVUploadProcessFormModel,
} from './tpr-csv-upload-process-form.provider';

@Component({
  selector: 'cca-tpr-csv-upload-process',
  templateUrl: './tpr-csv-upload-process.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    SelectComponent,
    DetailsComponent,
    MultipleFileInputComponent,
    TitleCasePipe,
    LoadingSpinnerComponent,
    RouterLink,
    PageHeadingComponent,
  ],
  providers: [PerformanceDataUploadProcessFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprCsvUploadProcessComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly authStore = inject(AuthStore);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly performanceDataReportingViewInfoService = inject(PerformanceDataReportingViewInfoService);

  protected readonly form = inject<TprCSVUploadProcessFormModel>(TPR_CSV_UPLOAD_PROCESS_FORM);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  private readonly interval = 10000;
  protected readonly downloadUrl = computed(() => generateDownloadUrl(this.taskId().toString()));

  private readonly availableTargetPeriods = toSignal(
    this.performanceDataReportingViewInfoService.getAvailableTargetPeriodsForPerformanceDataReporting('CCA_3'),
    { initialValue: [] },
  );

  private readonly targetPeriodTypeValue = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  protected readonly targetPeriodsOptions: Signal<GovukSelectOption<PerformanceDataReportTypeDTO>[]> = computed(() =>
    this.availableTargetPeriods().map((p) => ({
      value: p.targetPeriodType,
      text: p.targetPeriodType,
    })),
  );

  private readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId);
  protected readonly isUserAssignee = computed(() => this.authStore.select(selectUserId)() === this.assigneeUserId());

  protected readonly reportType = computed(
    () => this.availableTargetPeriods().find((tp) => tp.targetPeriodType === this.targetPeriodTypeValue())?.reportType,
  );

  protected readonly processingStatus = this.requestTaskStore.select(tprCSVUploadQuery.selectProcessingStatus);

  ngOnInit() {
    if (this.processingStatus() === 'IN_PROGRESS') {
      this.fetchTaskItemInfo().subscribe();
      return;
    }

    if (this.processingStatus() === 'COMPLETED') {
      this.router.navigate(['tpr-csv-upload/results'], { relativeTo: this.activatedRoute, replaceUrl: true });
    }
  }

  onSubmit() {
    if (!this.isUserAssignee()) return;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_PROCESSING',
        requestTaskId: this.taskId(),
        requestTaskActionPayload: {
          payloadType: 'PERFORMANCE_DATA_FACILITY_UPLOAD_PROCESSING_PAYLOAD',
          ...({
            performanceDataUpload: {
              files: fileUtils.toUUIDs(this.form.value.uploadedFiles),
              targetPeriodType: this.form.value.targetPeriodType,
              reportType: this.reportType(),
            },
          } as PerformanceDataFacilityDataUploadSubmitRequestTaskPayload),
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        catchError(() => {
          const message =
            'There was a problem with the upload. Please try again or contact cca-help@environment-agency.gov.uk';
          const uploadedFilesControl = this.form.controls.uploadedFiles;

          uploadedFilesControl.setErrors({
            ...(uploadedFilesControl.errors ?? {}),
            internalError: message,
          });

          uploadedFilesControl.markAsTouched();
          return EMPTY;
        }),
      )
      .subscribe((response: PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) => {
        this.requestTaskStore.setPayload(response);
        this.fetchTaskItemInfo().subscribe();
      });
  }

  private fetchTaskItemInfo(): Observable<unknown> {
    return timer(this.interval).pipe(
      take(1),
      switchMap(() => this.tasksService.getTaskItemInfoById(this.taskId())),
      takeUntilDestroyed(this.destroyRef),
      map((r) => r.requestTask.payload),
      switchMap((payload: PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) => {
        if (payload.processingStatus === 'IN_PROGRESS') return this.fetchTaskItemInfo();
        this.requestTaskStore.setPayload(payload);
        this.router.navigate(['tpr-csv-upload/results'], { relativeTo: this.activatedRoute, replaceUrl: true });
        return EMPTY;
      }),
    );
  }
}
