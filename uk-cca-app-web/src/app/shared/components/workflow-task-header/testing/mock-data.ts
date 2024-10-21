import { RequestInfoDTO, RequestTaskItemDTO, TargetUnitAccountHeaderInfoDTO } from 'cca-api';

export const mockWorkflowTaskHeaderInfo: TargetUnitAccountHeaderInfoDTO = {
  name: 'Target Unit Account 6',
  businessId: 'ADS_2-T00008',
  status: 'LIVE',
};

export const mockRequestInfo: RequestInfoDTO = {
  id: 'ADS_2-T00008-ATER-1',
  type: 'ADMIN_TERMINATION',
  competentAuthority: 'ENGLAND',
  accountId: 8,
};

export const mockRequestTaskItem: RequestTaskItemDTO = {
  requestTask: {},
  requestInfo: mockRequestInfo,
  allowedRequestTaskActions: [],
  userAssignCapable: true,
};
