import { TaskService } from '@netz/common/forms';

import { AdminTerminationSubmitRequestTaskPayload, CcaDecisionNotification } from 'cca-api';

import { AdminTerminationQuery } from '../+state/admin-termination.selectors';
import { AdminTerminationTaskApiService } from './admin-termination-task-api.service';

export class AdminTerminationTaskService extends TaskService {
  get payload(): AdminTerminationSubmitRequestTaskPayload {
    return this.store.select(AdminTerminationQuery.selectAdminTerminationPayload)();
  }

  set payload(payload: AdminTerminationSubmitRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as AdminTerminationTaskApiService).notifyOperator(notificationPayload);
  }
}
