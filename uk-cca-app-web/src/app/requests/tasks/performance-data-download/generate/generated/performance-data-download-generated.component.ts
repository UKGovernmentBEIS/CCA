import { NgOptimizedImage } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { ErrorMessageTypePipe } from '@requests/common';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { performanceDataDownloadQuery } from '../../+state/performance-data-download.selectors';
import { SectorAssociationInfoSummaryComponent } from '../sector-association-info-summary/sector-association-info-summary.component';

@Component({
  selector: 'cca-performance-data-download-generated',
  standalone: true,
  imports: [
    NotificationBannerComponent,
    NgOptimizedImage,
    ButtonDirective,
    PendingButtonDirective,
    RouterLink,
    ErrorMessageTypePipe,
    ReactiveFormsModule,
    SectorAssociationInfoSummaryComponent,
  ],
  templateUrl: './performance-data-download-generated.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataDownloadGeneratedComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  readonly sectorId = input<string>();
  readonly sectorName = input<string>();
  readonly targetPeriod = input<string>();
  readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  readonly zipFile = this.requestTaskStore.select(performanceDataDownloadQuery.selectZipFile);
  readonly errorsFile = this.requestTaskStore.select(performanceDataDownloadQuery.selectErrorsFile);
  readonly errorMessage = this.requestTaskStore.select(performanceDataDownloadQuery.selectErrorMessage);

  onComplete() {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_DOWNLOAD_COMPLETE',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
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
      .subscribe(() =>
        this.router.navigate(['performance-data-download/confirmation'], { relativeTo: this.activatedRoute }),
      );
  }
}
