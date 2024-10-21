import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';
import { UnderlyingAgreementDecisionRequestActionPayload } from '@requests/common';

import {
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  Determination,
  FileInfoDTO,
  RequestActionUserInfo,
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestActionState, UnderlyingAgreementDecisionRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as UnderlyingAgreementDecisionRequestActionPayload,
  );

const selectBusinessId: StateSelector<RequestActionState, string> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.businessId,
);

const selectDecisionNotification: StateSelector<RequestActionState, CcaDecisionNotification> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.decisionNotification,
);

const selectDefaultContacts: StateSelector<RequestActionState, DefaultNoticeRecipient[]> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.defaultContacts,
);

const selectRequestActionUserInfo: StateSelector<RequestActionState, { [key: string]: RequestActionUserInfo }> =
  createDescendingSelector(selectPayload, (payload) => payload.usersInfo);

const selectOfficialNotice: StateSelector<RequestActionState, FileInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.officialNotice,
);

const selectDetermination: StateSelector<RequestActionState, Determination> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.determination,
);

const selectReviewSectionsCompleted: StateSelector<RequestActionState, { [key: string]: string }> =
  createDescendingSelector(
    selectPayload,
    (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.reviewSectionsCompleted,
  );

const selectSubtaskDecision = (group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']) =>
  createDescendingSelector(
    selectPayload,
    (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.reviewGroupDecisions[group],
  );

const selectFacilitySubtaskDecision = (facility: string) =>
  createDescendingSelector(
    selectPayload,
    (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.facilitiesReviewGroupDecisions[facility],
  );

const selectReviewAttachments: StateSelector<RequestActionState, { [key: string]: string }> = createDescendingSelector(
  selectPayload,
  (payload: UnderlyingAgreementDecisionRequestActionPayload) => payload.reviewAttachments,
);

export const underlyingAgreementReviewedRequestActionQuery = {
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
};
