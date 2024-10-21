import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTargetUnitDetails,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

export class ReviewTargetUnitDetailsPayloadMutator extends PayloadMutator {
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAApplicationRequestTaskPayload,
    step,
    userInput: UnderlyingAgreementTargetUnitDetails,
  ): Observable<UNAApplicationRequestTaskPayload> {
    return applyTargetUnitDetails(currentPayload, this.subtask, userInput);
  }
}
