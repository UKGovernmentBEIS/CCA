import { inject } from '@angular/core';

import { AuthStore, selectUserState } from '@netz/common/auth';
import { RelatedActionsMap } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

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
  UNDERLYING_AGREEMENT_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  UNDERLYING_AGREEMENT_VARIATION_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCEL_APPLICATION: {
    text: 'Cancel task',
    path: ['cancel'],
  },
  FACILITY_AUDIT_CANCEL_APPLICATION: { text: 'Cancel task', path: ['cancel'] },
};
