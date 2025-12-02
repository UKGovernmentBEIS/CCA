import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map, switchMap, take } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ItemLinkPipe } from '@netz/common/pipes';
import { ButtonDirective } from '@netz/govuk-components';
import { StatusPipe } from '@shared/pipes';

import {
  RequestCreateValidationResult,
  RequestItemsService,
  RequestsService,
  TargetUnitAccountDetailsDTO,
  UserStateDTO,
} from 'cca-api';

import {
  processActionsDetailsTypesMap,
  taskWorkflowContentDisplayMap,
  userRoleWorkflowAccessMap,
  WorkflowDisplayContent,
} from './start-new-task.types';

@Component({
  selector: 'cca-start-new-task',
  templateUrl: './start-new-task.component.html',
  imports: [PageHeadingComponent, PendingButtonDirective, ButtonDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StartNewTaskComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestsService = inject(RequestsService);
  private readonly requestItemsService = inject(RequestItemsService);
  private readonly authStore = inject(AuthStore);

  private readonly id =
    this.activatedRoute.snapshot.paramMap.get('targetUnitId') ?? this.activatedRoute.snapshot.paramMap.get('sectorId');

  private readonly resourceType = this.activatedRoute.snapshot.paramMap.get('targetUnitId')
    ? 'ACCOUNT'
    : 'SECTOR_ASSOCIATION';

  protected readonly returnTo =
    this.activatedRoute.snapshot.data.targetUnit?.targetUnitAccountDetails?.name ??
    `${this.activatedRoute.snapshot.data.details.sectorAssociationDetails.acronym} - ${this.activatedRoute.snapshot.data.details.sectorAssociationDetails.commonName}`;

  protected readonly availableWorkflows = toSignal(
    this.requestsService
      .getAvailableWorkflows(this.resourceType, this.id)
      .pipe(map((response) => this.mapToWorkflowDisplayContent(response, this.authStore.select(selectUserRoleType)()))),
  );

  mapToWorkflowDisplayContent(
    validationResults: Record<string, RequestCreateValidationResult>,
    userRole: UserStateDTO['roleType'],
  ): WorkflowDisplayContent[] {
    const workflowOrder = [
      'UNDERLYING_AGREEMENT_VARIATION',
      'PERFORMANCE_DATA_DOWNLOAD',
      'PERFORMANCE_DATA_UPLOAD',
      'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
      'ADMIN_TERMINATION',
    ];

    return Object.entries(validationResults)
      .filter(([type]) => this.isWorkflowAvailableForRole(type, userRole))
      .map(([type, result]) => this.createWorkflowDisplayContent(type, result))
      .sort((a, b) => workflowOrder.indexOf(a.type) - workflowOrder.indexOf(b.type));
  }

  isWorkflowAvailableForRole(type: string, userRole: UserStateDTO['roleType']): boolean {
    return userRoleWorkflowAccessMap[userRole]?.includes(type) ?? false;
  }

  createWorkflowDisplayContent(type: string, result: RequestCreateValidationResult): WorkflowDisplayContent {
    const workflowContent = taskWorkflowContentDisplayMap[type] || { title: '', button: '', type: '', errors: [] };
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
      const accountStatusString = new StatusPipe().transform(status)?.toUpperCase();
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
        const link = items?.length === 1 ? new ItemLinkPipe().transform(items[0]) : ['/dashboard'];

        this.router.navigate(link, { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
