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

export type WorkflowHistoryDetailsResponse = {
  workflowDetails: RequestDetailsDTO;
  requestItems: ItemDTOResponse;
  requestActions: RequestActionInfoDTO[];
};

export const WorkflowHistoryDetailsResolver: ResolveFn<WorkflowHistoryDetailsResponse> = (route) => {
  const requestsService = inject(RequestsService);
  const requestItemsService = inject(RequestItemsService);
  const requestActionsService = inject(RequestActionsService);

  const paymentRequestId = route.paramMap.get('id');

  return combineLatest({
    workflowDetails: requestsService.getRequestDetailsById(paymentRequestId),
    requestItems: requestItemsService.getItemsByRequest(paymentRequestId),
    requestActions: requestActionsService.getRequestActionsByRequestId(paymentRequestId),
  });
};
