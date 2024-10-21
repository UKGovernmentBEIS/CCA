import { TaskService } from '@netz/common/forms';

import { AdminTerminationWithdrawRequestTaskPayload, CcaDecisionNotification } from 'cca-api';

import { AdminTerminationWithdrawQuery } from '../+state/withdraw-admin-termination.selectors';
import { WithdrawAdminTerminationTaskApiService } from './withdraw-admin-termination-task-api.service';

export class WithdrawAdminTerminationTaskService extends TaskService {
  get payload(): AdminTerminationWithdrawRequestTaskPayload {
    return this.store.select(AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationPayload)();
  }

  set payload(payload: AdminTerminationWithdrawRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as WithdrawAdminTerminationTaskApiService).notifyOperator(notificationPayload);
  }
}
