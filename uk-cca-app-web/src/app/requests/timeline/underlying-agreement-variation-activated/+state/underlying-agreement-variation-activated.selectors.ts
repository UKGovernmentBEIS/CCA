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

const selectOfficialNotices: StateSelector<RequestActionState, FileInfoDTO[]> = createDescendingSelector(
  selectPayload,
  (payload) => payload.officialNotices,
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

const selectUnderlyingAgreementDocuments: StateSelector<
  RequestActionState,
  Record<string, FileInfoDTO>
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreementDocuments);

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
  selectOfficialNotices,
  selectUsersInfo,
  selectDefaultContacts,
  selectDecisionNotification,
  selectUnderlyingAgreementDocuments,
  selectUnderlyingAgreementActivationDetails,
  selectUnderlyingAgreementActivationAttachments,
};
