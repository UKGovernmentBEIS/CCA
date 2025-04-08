import { createDescendingSelector, requestActionQuery, RequestActionState, StateSelector } from '@netz/common/store';

import {
  CcaDecisionNotification,
  DefaultNoticeRecipient,
  FileInfoDTO,
  RequestActionUserInfo,
  UnderlyingAgreementActivationDetails,
  UnderlyingAgreementVariationActivatedRequestActionPayload,
} from 'cca-api';

const selectPayload: StateSelector<RequestActionState, UnderlyingAgreementVariationActivatedRequestActionPayload> =
  createDescendingSelector(
    requestActionQuery.selectActionPayload,
    (actionPayload) => actionPayload as UnderlyingAgreementVariationActivatedRequestActionPayload,
  );

const selectOfficialNotice: StateSelector<RequestActionState, FileInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload) => payload.officialNotice,
);

const selectUsersInfo: StateSelector<
  RequestActionState,
  Record<string, RequestActionUserInfo>
> = createDescendingSelector(selectPayload, (payload) => payload.usersInfo);

const selectDefaultContacts: StateSelector<
  RequestActionState,
  Array<DefaultNoticeRecipient>
> = createDescendingSelector(selectPayload, (payload) => payload.defaultContacts);

const selectDecisionNotification: StateSelector<RequestActionState, CcaDecisionNotification> = createDescendingSelector(
  selectPayload,
  (payload) => payload.decisionNotification,
);

const selectUnderlyingAgreementDocument: StateSelector<RequestActionState, FileInfoDTO> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreementDocument,
);

const selectUnderlyingAgreementActivationDetails: StateSelector<
  RequestActionState,
  UnderlyingAgreementActivationDetails
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementActivationDetails);

const selectUnderlyingAgreementActivationAttachments: StateSelector<
  RequestActionState,
  Record<string, string>
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementActivationAttachments);

export const underlyingAgreementVariationActivatedQuery = {
  selectPayload,
  selectOfficialNotice,
  selectUsersInfo,
  selectDefaultContacts,
  selectDecisionNotification,
  selectUnderlyingAgreementDocument,
  selectUnderlyingAgreementActivationDetails,
  selectUnderlyingAgreementActivationAttachments,
};
