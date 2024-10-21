import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import {
  applyTargetUnitDetails,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  UNAVariationRequestTaskPayload,
} from '@requests/common';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

export class ReviewTargetUnitDetailsPayloadMutator extends PayloadMutator {
  override subtask = REVIEW_TARGET_UNIT_DETAILS_SUBTASK;

  /**
   * @param currentPayload
   * @param userInput The form value of each step
   */
  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step,
    userInput: UnderlyingAgreementTargetUnitDetails,
  ): Observable<UNAVariationRequestTaskPayload> {
    return applyTargetUnitDetails(
      currentPayload,
      this.subtask,
      userInput,
    ) as Observable<UNAVariationRequestTaskPayload>;
  }
}
