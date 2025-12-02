import { RequestCreateActionProcessDTO, RequestDetailsDTO, UserStateDTO } from 'cca-api';

export interface WorkflowDisplayContent {
  title: string;
  button: string;
  hint?: string;
  type: RequestCreateActionProcessDTO['requestType'];
  errors: string[];
}

export const processActionsDetailsTypesMap: Partial<Record<RequestDetailsDTO['requestType'], string>> = {
  ADMIN_TERMINATION: 'admin Termination',
  UNDERLYING_AGREEMENT_VARIATION: 'variation',
  PERFORMANCE_DATA_DOWNLOAD: 'download performance data',
  PERFORMANCE_DATA_UPLOAD: 'upload performance data',
  PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD: 'upload PAT spreadsheets',
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING: 'CCA3 Migration',
};

export const userRoleWorkflowAccessMap: Record<UserStateDTO['roleType'], string[]> = {
  REGULATOR: ['ADMIN_TERMINATION'],
  OPERATOR: [],
  SECTOR_USER: [
    'UNDERLYING_AGREEMENT_VARIATION',
    'PERFORMANCE_DATA_DOWNLOAD',
    'PERFORMANCE_DATA_UPLOAD',
    'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
  ],
  VERIFIER: [],
};

export const taskWorkflowContentDisplayMap: Record<RequestDetailsDTO['requestType'], WorkflowDisplayContent> = {
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
  PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD: {
    title: 'Upload PAT spreadsheets',
    button: 'Start PAT Spreadsheets upload',
    type: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
    errors: [],
  },
};
