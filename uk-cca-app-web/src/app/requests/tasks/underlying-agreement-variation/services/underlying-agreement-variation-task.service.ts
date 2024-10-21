import { Injectable } from '@angular/core';

import { TaskService } from '@netz/common/forms';
import { UNAVariationRequestTaskPayload, underlyingAgreementQuery } from '@requests/common';

@Injectable()
export class UnderlyingAgreementVariationTaskService extends TaskService {
  get payload(): UNAVariationRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)() as UNAVariationRequestTaskPayload;
  }

  set payload(payload: UNAVariationRequestTaskPayload) {
    this.store.setPayload(payload);
  }
}
