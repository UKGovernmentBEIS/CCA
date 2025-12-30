import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { combineLatest } from 'rxjs';

import { RequestActionInfoDTO, RequestActionsService, RequestDetailsDTO, RequestsService } from 'cca-api';

export type WorkflowHistoryDetailsResponse = {
  workflowDetails: RequestDetailsDTO;
  requestActions: RequestActionInfoDTO[];
};

export const WorkflowHistoryDetailsResolver: ResolveFn<WorkflowHistoryDetailsResponse> = (route) => {
  const requestsService = inject(RequestsService);
  const requestActionsService = inject(RequestActionsService);

  const paymentRequestId = route.paramMap.get('workflowId');

  return combineLatest({
    workflowDetails: requestsService.getRequestDetailsById(paymentRequestId),
    requestActions: requestActionsService.getRequestActionsByRequestId(paymentRequestId),
  });
};
