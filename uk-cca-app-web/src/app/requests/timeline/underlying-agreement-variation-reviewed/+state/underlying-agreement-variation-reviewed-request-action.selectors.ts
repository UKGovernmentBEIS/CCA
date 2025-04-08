import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';
import { UnderlyingAgreementVariationDecisionRequestActionPayload } from '@requests/common';

import {
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  Determination,
  FileInfoDTO,
  RequestActionUserInfo,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestActionState, UnderlyingAgreementVariationDecisionRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as UnderlyingAgreementVariationDecisionRequestActionPayload,
  );

const selectBusinessId: StateSelector<RequestActionState, string> = createDescendingSelector(
  selectPayload,
  (payload) => payload.businessId,
);

const selectDecisionNotification: StateSelector<RequestActionState, CcaDecisionNotification> = createDescendingSelector(
  selectPayload,
  (payload) => payload.decisionNotification,
);

const selectDefaultContacts: StateSelector<RequestActionState, DefaultNoticeRecipient[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload.defaultContacts,
);

const selectRequestActionUserInfo: StateSelector<
  RequestActionState,
  Record<string, RequestActionUserInfo>
> = createDescendingSelector(selectPayload, (payload) => payload.usersInfo);

const selectOfficialNotice: StateSelector<RequestActionState, FileInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload) => payload.officialNotice,
);

const selectDetermination: StateSelector<RequestActionState, Determination> = createDescendingSelector(
  selectPayload,
  (payload) => payload.determination,
);

const selectReviewSectionsCompleted: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.reviewSectionsCompleted);

const selectSubtaskDecision = (
  group: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
) => createDescendingSelector(selectPayload, (payload) => payload.reviewGroupDecisions[group]);

const selectFacilitySubtaskDecision = (facility: string) =>
  createDescendingSelector(selectPayload, (payload) => payload.facilitiesReviewGroupDecisions[facility]);

const selectReviewAttachments: StateSelector<RequestActionState, Record<string, string>> = createDescendingSelector(
  selectPayload,
  (payload) => payload.reviewAttachments,
);

const selectUnderlyingAgreement: StateSelector<RequestActionState, UnderlyingAgreementVariationPayload> =
  createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreement);

const selectVariationDetails: StateSelector<RequestActionState, UnderlyingAgreementVariationDetails> =
  createDescendingSelector(selectUnderlyingAgreement, (payload) => payload.underlyingAgreementVariationDetails);

export const underlyingAgreementVariationReviewedRequestActionQuery = {
  selectPayload,
  selectBusinessId,
  selectDecisionNotification,
  selectDefaultContacts,
  selectRequestActionUserInfo,
  selectOfficialNotice,
  selectDetermination,
  selectReviewSectionsCompleted,
  selectSubtaskDecision,
  selectFacilitySubtaskDecision,
  selectReviewAttachments,
  selectUnderlyingAgreement,
  selectVariationDetails,
};
