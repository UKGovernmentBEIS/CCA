import { Injectable } from '@angular/core';

import { TaskService } from '@netz/common/forms';
import { UNAApplicationRequestTaskPayload, underlyingAgreementQuery } from '@requests/common';

@Injectable()
export class UnderlyingAgreementApplicationTaskService extends TaskService {
  get payload(): UNAApplicationRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)();
  }

  set payload(payload: UNAApplicationRequestTaskPayload) {
    this.store.setPayload(payload);
  }
}
