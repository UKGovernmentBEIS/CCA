import { ChangeDetectionStrategy, Component, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, switchMap, take } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ItemLinkPipe } from '@netz/common/pipes';
import { ButtonDirective } from '@netz/govuk-components';
import { AccountStatusPipe } from '@shared/pipes';

import {
  RequestCreateValidationResult,
  RequestItemsService,
  RequestsService,
  TargetUnitAccountDetailsDTO,
  UserStateDTO,
} from 'cca-api';

import {
  processActionsDetailsTypesMap,
  userRoleWorkflowAccessMap,
  WorkflowDisplayContent,
} from './start-new-task.types';

@Component({
  selector: 'cca-start-new-task',
  standalone: true,
  imports: [PageHeadingComponent, PendingButtonDirective, ButtonDirective, RouterLink],
  templateUrl: './start-new-task.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StartNewTaskComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestsService = inject(RequestsService);
  private readonly requestItemsService = inject(RequestItemsService);
  private readonly authStore = inject(AuthStore);

  id: string =
    this.activatedRoute.snapshot.paramMap.get('targetUnitId') ?? this.activatedRoute.snapshot.paramMap.get('sectorId');

  resourceType: string = this.activatedRoute.snapshot.paramMap.get('targetUnitId') ? 'ACCOUNT' : 'SECTOR_ASSOCIATION';

  returnTo: string =
    this.activatedRoute.snapshot.data.targetUnit?.targetUnitAccountDetails?.name ??
    `${this.activatedRoute.snapshot.data.details.sectorAssociationDetails.acronym} - ${this.activatedRoute.snapshot.data.details.sectorAssociationDetails.commonName}`;

  availableWorkflows: Signal<WorkflowDisplayContent[]> = toSignal(
    this.requestsService
      .getAvailableWorkflows(this.resourceType, this.id)
      .pipe(map((response) => this.mapToWorkflowDisplayContent(response, this.authStore.select(selectUserRoleType)()))),
  );

  private readonly taskWorkflowContentDisplayMap: Record<string, WorkflowDisplayContent> = {
    ADMIN_TERMINATION: {
      title: 'Admin termination',
      button: 'Start admin termination',
      hint: 'Terminate the underlying agreement. The target unit account will be closed once the admin termination is complete.',
      type: 'ADMIN_TERMINATION',
      errors: [],
    },
    UNDERLYING_AGREEMENT_VARIATION: {
      title: 'Make a permanent change to your underlying agreement',
      button: 'Start a variation',
      type: 'UNDERLYING_AGREEMENT_VARIATION',
      errors: [],
    },
    PERFORMANCE_DATA_DOWNLOAD: {
      title: 'Download target period reporting (TPR) spreadsheets',
      hint: 'Generate a zip file that contains the TPR spreadsheets. You will be able to edit the data in the spreadsheets.',
      button: 'Start TPR spreadsheets download',
      type: 'PERFORMANCE_DATA_DOWNLOAD',
      errors: [],
    },
    PERFORMANCE_DATA_UPLOAD: {
      title: 'Upload target period reporting (TPR) spreadsheets',
      button: 'Start TPR spreadsheets upload',
      type: 'PERFORMANCE_DATA_UPLOAD',
      errors: [],
    },
  };

  mapToWorkflowDisplayContent(
    validationResults: Record<string, RequestCreateValidationResult>,
    userRole: UserStateDTO['roleType'],
  ): WorkflowDisplayContent[] {
    return Object.entries(validationResults)
      .filter(([type]) => this.isWorkflowAvailableForRole(type, userRole))
      .map(([type, result]) => this.createWorkflowDisplayContent(type, result));
  }

  isWorkflowAvailableForRole(type: string, userRole: UserStateDTO['roleType']): boolean {
    return userRoleWorkflowAccessMap[userRole]?.includes(type) ?? false;
  }

  createWorkflowDisplayContent(type: string, result: RequestCreateValidationResult): WorkflowDisplayContent {
    const workflowContent = this.taskWorkflowContentDisplayMap[type] || { title: '', button: '', type: '', errors: [] };
    return {
      ...workflowContent,
      type,
      errors: result.valid ? [] : this.createErrorMessages(type, result),
    };
  }

  createErrorMessages(requestType: string, result: RequestCreateValidationResult): string[] {
    const status = result?.accountStatus as unknown as TargetUnitAccountDetailsDTO['status'];
    const typeString = processActionsDetailsTypesMap[requestType];

    if (status && !result?.applicableAccountStatuses?.includes(status)) {
      const accountStatusString = new AccountStatusPipe().transform(status)?.toUpperCase();
      return [`You cannot start the ${typeString} while the account status is ${accountStatusString}.`];
    } else {
      return result.requests.map((r) => this.createErrorMessage(requestType, r));
    }
  }

  createErrorMessage(currentRequestType: string, resultRequestType: string): string {
    const currentRequestTypeString = processActionsDetailsTypesMap[currentRequestType];
    const resultRequestTypeString = processActionsDetailsTypesMap[resultRequestType];

    if (currentRequestType === resultRequestType) {
      return `You cannot start the ${currentRequestTypeString} process as it is already in progress.`;
    } else {
      return `You cannot start the ${currentRequestTypeString} process while the ${resultRequestTypeString} is in progress.`;
    }
  }

  onRequestButtonClick(requestType: string) {
    this.requestsService
      .processRequestCreateAction(
        {
          requestType: requestType,
          requestCreateActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
          },
        },
        this.id,
      )
      .pipe(
        take(1),
        switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
      )
      .subscribe(({ items }) => {
        const link = items?.length == 1 ? new ItemLinkPipe().transform(items[0]) : ['/dashboard'];

        this.router.navigate(link, { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
