import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { catchError, EMPTY, map, Observable, switchMap, take, timer } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent } from '@netz/govuk-components';
import { LoadingSpinnerComponent, MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';
import { fileUtils, generateDownloadUrl } from '@shared/utils';

import {
  FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload,
  FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload,
  TasksService,
} from 'cca-api';

import { PAT_CSV_UPLOAD_ERROR_MESSAGES, PatCsvUploadErrorCode } from '../pat-csv-upload.errors';
import { patCsvUploadQuery } from '../performance-account-template-csv-upload.selectors';
import {
  PAT_CSV_UPLOAD_PROCESS_FORM,
  PatCsvUploadProcessFormModel,
  PatCsvUploadProcessFormProvider,
} from './pat-csv-upload-process-form.provider';

@Component({
  selector: 'cca-pat-csv-upload-process',
  templateUrl: './pat-csv-upload-process.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    DetailsComponent,
    MultipleFileInputComponent,
    LoadingSpinnerComponent,
    RouterLink,
    PageHeadingComponent,
  ],
  providers: [PatCsvUploadProcessFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatCsvUploadProcessComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly authStore = inject(AuthStore);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly destroyRef = inject(DestroyRef);

  readonly form = inject<PatCsvUploadProcessFormModel>(PAT_CSV_UPLOAD_PROCESS_FORM);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  private readonly interval = 10000;
  protected readonly downloadUrl = computed(() => generateDownloadUrl(this.taskId().toString()));

  private readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId);
  protected readonly isUserAssignee = computed(() => this.authStore.select(selectUserId)() === this.assigneeUserId());

  protected readonly targetYear = this.requestTaskStore.select(
    patCsvUploadQuery.selectPerformanceAccountTemplateDataUpload,
  )()?.targetYear;

  protected readonly processingStatus = this.requestTaskStore.select(patCsvUploadQuery.selectProcessingStatus);

  ngOnInit() {
    if (this.processingStatus() === 'IN_PROGRESS') {
      this.fetchTaskItemInfo().subscribe();
      return;
    }

    if (this.processingStatus() === 'COMPLETED') {
      this.router.navigate(['pat-csv-upload/results'], { relativeTo: this.activatedRoute, replaceUrl: true });
    }
  }

  onSubmit() {
    if (!this.isUserAssignee()) return;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING',
        requestTaskId: this.taskId(),
        requestTaskActionPayload: {
          payloadType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_PROCESSING_PAYLOAD',
          performanceAccountTemplateDataUpload: {
            files: fileUtils.toUUIDs(this.form.value.uploadedFiles),
            targetYear: this.targetYear,
          },
        } as FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest<never>(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        catchError((err) => {
          this.showUploadError(
            PAT_CSV_UPLOAD_ERROR_MESSAGES[err.error?.code as PatCsvUploadErrorCode] ??
              'There was a problem with the upload. Please try again or contact cca-help@environment-agency.gov.uk',
          );
          return EMPTY;
        }),
      )
      .subscribe((response: FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) => {
        this.requestTaskStore.setPayload(response);
        this.fetchTaskItemInfo().subscribe();
      });
  }

  private showUploadError(message: string): void {
    const uploadedFilesControl = this.form.controls.uploadedFiles;

    uploadedFilesControl.setErrors({
      ...(uploadedFilesControl.errors ?? {}),
      responseError: message,
    });

    this.form.markAllAsTouched();
    this.form.updateValueAndValidity();
  }

  private fetchTaskItemInfo(): Observable<unknown> {
    return timer(this.interval).pipe(
      take(1),
      switchMap(() => this.tasksService.getTaskItemInfoById(this.taskId())),
      takeUntilDestroyed(this.destroyRef),
      map((r) => r.requestTask.payload),
      switchMap((payload: FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) => {
        if (payload.processingStatus === 'IN_PROGRESS') return this.fetchTaskItemInfo();
        this.requestTaskStore.setPayload(payload);
        this.router.navigate(['pat-csv-upload/results'], { relativeTo: this.activatedRoute, replaceUrl: true });
        return EMPTY;
      }),
    );
  }
}
