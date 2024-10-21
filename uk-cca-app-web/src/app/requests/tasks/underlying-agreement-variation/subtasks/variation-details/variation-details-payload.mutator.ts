import { Observable } from 'rxjs';

import { PayloadMutator } from '@netz/common/forms';
import { applyVariationDetails, UNAVariationRequestTaskPayload, VARIATION_DETAILS_SUBTASK } from '@requests/common';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

export class VariationDetailsPayloadMutator extends PayloadMutator {
  override subtask = VARIATION_DETAILS_SUBTASK;

  apply(
    currentPayload: UNAVariationRequestTaskPayload,
    step,
    userInput: UnderlyingAgreementVariationDetails,
  ): Observable<UNAVariationRequestTaskPayload> {
    return applyVariationDetails(currentPayload, this.subtask, userInput);
  }
}
