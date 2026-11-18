import { inject } from '@angular/core';

import { AuthStore, selectUserState } from '@netz/common/auth';
import { RelatedActionsMap } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { RequestTaskDTO } from 'cca-api';

const nonComplianceCloseTaskPath = (_taskId: RequestTaskDTO['id'], taskType?: RequestTaskDTO['type']): string[] => {
  switch (taskType) {
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT':
      return ['notice-of-intent', 'close-task'];
    case 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT':
      return ['enforcement-response-notice', 'close-task'];
    case 'NON_COMPLIANCE_CONCLUSION_SUBMIT':
      return ['non-compliance-conclusion', 'close-task'];
    default:
      return ['close-task'];
  }
};

export function createIsEditableResolver(): () => boolean {
  const store = inject(RequestTaskStore);
  const authStore = inject(AuthStore);

  return () => {
    const assigneeUserId = store.select(requestTaskQuery.selectAssigneeUserId)();
    const allowedRequestTaskActions = store.select(requestTaskQuery.selectAllowedRequestTaskActions)();
    const userId = authStore.select(selectUserState)()?.userId;

    return assigneeUserId === userId && allowedRequestTaskActions.length > 0;
  };
}

export const taskRelatedActionsMap: RelatedActionsMap = {
  ADMIN_TERMINATION_UPLOAD_ATTACHMENT: { text: '', path: [''] },
  ADMIN_TERMINATION_SUBMIT_APPLICATION: { text: '', path: [''] },
  ADMIN_TERMINATION_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  ADMIN_TERMINATION_SAVE_APPLICATION: { text: '', path: [''] },
  ADMIN_TERMINATION_NOTIFY_OPERATOR_FOR_DECISION: { text: '', path: [''] },
  ADMIN_TERMINATION_WITHDRAW_SAVE_APPLICATION: { text: '', path: [''] },
  ADMIN_TERMINATION_FINAL_DECISION_SAVE_APPLICATION: { text: '', path: [''] },
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR: { text: '', path: [''] },
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_REQUEST_PEER_REVIEW: { text: '', path: [''] },
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION: { text: '', path: [''] },
  NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION: { text: '', path: [''] },
  NON_COMPLIANCE_UPLOAD_ATTACHMENT: { text: '', path: [''] },
  UNDERLYING_AGREEMENT_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  UNDERLYING_AGREEMENT_VARIATION_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCEL_APPLICATION: {
    text: 'Cancel task',
    path: ['cancel'],
  },
  FACILITY_AUDIT_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  NON_COMPLIANCE_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION: { text: '', path: [''] },
  NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR: { text: '', path: [''] },
  NON_COMPLIANCE_CONCLUSION_COMPLETE_APPLICATION: { text: '', path: [''] },
  NON_COMPLIANCE_CLOSE_APPLICATION: { text: 'Close task', path: nonComplianceCloseTaskPath },
  NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS: {
    text: 'Provide appeal details',
    path: ['non-compliance-conclusion', 'provide-appeal-details'],
  },
  PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION: {
    text: 'Refresh baseline data',
    path: ['target-period-reporting-form', 'refresh-baseline-data'],
  },
  PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE: { text: 'Close task', path: ['tpr-csv-upload', 'close-task'] },
};
