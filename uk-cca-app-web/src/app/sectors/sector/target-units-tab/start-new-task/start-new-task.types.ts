import { RequestCreateActionProcessDTO, RequestDetailsDTO, UserStateDTO } from 'cca-api';

export const processActionsDetailsTypesMap: Partial<Record<RequestDetailsDTO['requestType'], string>> = {
  ADMIN_TERMINATION: 'admin Termination',
  UNDERLYING_AGREEMENT_VARIATION: 'variation',
};

export const userRoleWorkflowAccessMap: Record<UserStateDTO['roleType'], string[]> = {
  REGULATOR: ['ADMIN_TERMINATION'],
  OPERATOR: [],
  SECTOR_USER: ['UNDERLYING_AGREEMENT_VARIATION'],
  VERIFIER: [],
};

export interface WorkflowDisplayContent {
  title: string;
  button: string;
  hint?: string;
  type: RequestCreateActionProcessDTO['requestType'];
  errors: string[];
}
