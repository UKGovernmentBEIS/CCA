import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'cca-api';

export const cancelActionMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {};
