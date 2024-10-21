import { TaskService } from '@netz/common/forms';

import { AdminTerminationFinalDecisionRequestTaskPayload, CcaDecisionNotification } from 'cca-api';

import { AdminTerminationFinalDecisionQuery } from '../+state/admin-termination-final-decision.selectors';
import { AdminTerminationFinalDecisionTaskApiService } from './admin-termination-final-decision-task-api.service';

export class AdminTerminationFinalDecisionTaskService extends TaskService {
  get payload(): AdminTerminationFinalDecisionRequestTaskPayload {
    return this.store.select(AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionPayload)();
  }

  set payload(payload: AdminTerminationFinalDecisionRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as AdminTerminationFinalDecisionTaskApiService).notifyOperator(notificationPayload);
  }
}
