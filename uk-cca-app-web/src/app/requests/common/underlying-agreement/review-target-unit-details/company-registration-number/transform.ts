import { produce } from 'immer';

import {
  AccountAddressDTO,
  CompanyProfileDTO,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementVariationRegulatorLedSavePayload,
  UnderlyingAgreementVariationReviewSavePayload,
} from 'cca-api';

import { CompanyNumberState } from '../../types';

export function updateTUDetails(
  payload: UnderlyingAgreementApplySavePayload,
  companyNumberState: CompanyNumberState,
  companyProfile: CompanyProfileDTO | null,
): UnderlyingAgreementApplySavePayload {
  const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails = {
      ...draft.underlyingAgreementTargetUnitDetails,
      companyRegistrationNumber: companyProfile?.registrationNumber ?? companyNumberState.companyRegistrationNumber,
      isCompanyRegistrationNumber: companyNumberState.isCompanyRegistrationNumber,
      registrationNumberMissingReason: companyNumberState.registrationNumberMissingReason,
      operatorName: companyProfile?.name ?? null,
      operatorType: null,
      operatorAddress: address ?? null,
    };
  });
}

export function updateVariationReviewTUDetails(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  companyNumberState: CompanyNumberState,
  companyProfile: CompanyProfileDTO | null,
): UnderlyingAgreementVariationReviewSavePayload {
  const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails = {
      ...draft.underlyingAgreementTargetUnitDetails,
      companyRegistrationNumber: companyProfile?.registrationNumber ?? companyNumberState.companyRegistrationNumber,
      isCompanyRegistrationNumber: companyNumberState.isCompanyRegistrationNumber,
      registrationNumberMissingReason: companyNumberState.registrationNumberMissingReason,
      operatorName: companyProfile?.name ?? null,
      operatorType: null,
      operatorAddress: address ?? null,
      responsiblePersonDetails: {
        ...draft.underlyingAgreementTargetUnitDetails.responsiblePersonDetails,
      },
    };
  });
}

export function updateTUDetailsRegulatorLed(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  companyNumberState: CompanyNumberState,
  companyProfile: CompanyProfileDTO | null,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  const address: AccountAddressDTO = { ...companyProfile?.address, country: null };

  return produce(payload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails = {
      ...draft.underlyingAgreementTargetUnitDetails,
      companyRegistrationNumber: companyProfile?.registrationNumber ?? companyNumberState.companyRegistrationNumber,
      isCompanyRegistrationNumber: companyNumberState.isCompanyRegistrationNumber,
      registrationNumberMissingReason: companyNumberState.registrationNumberMissingReason,
      operatorName: companyProfile?.name ?? null,
      operatorType: null,
      operatorAddress: address ?? null,
    };
  });
}
