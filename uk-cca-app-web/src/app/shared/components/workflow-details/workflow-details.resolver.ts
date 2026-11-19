import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { combineLatest } from 'rxjs';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestDetailsDTO,
  RequestItemsService,
  RequestsService,
} from 'cca-api';

export type ResolvedWorkflowDetails = {
  workflowDetails: RequestDetailsDTO;
  requestItems: ItemDTOResponse;
  requestActions: RequestActionInfoDTO[];
};

export const WorkflowDetailsResolver: ResolveFn<ResolvedWorkflowDetails> = (route) => {
  const workflowId = route.paramMap.get('workflowId');
  const requestsService = inject(RequestsService);
  const requestItemsService = inject(RequestItemsService);
  const requestActionsService = inject(RequestActionsService);

  return combineLatest({
    workflowDetails: requestsService.getRequestDetailsById(workflowId),
    requestItems: requestItemsService.getItemsByRequest(workflowId),
    requestActions: requestActionsService.getRequestActionsByRequestId(workflowId),
  });
};
