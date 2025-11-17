import {
  AuthorisationAndAdditionalEvidence,
  BaselineData,
  Facility,
  TargetComposition,
  TargetPeriod6Details,
  UnderlyingAgreementTargetUnitDetails,
} from 'cca-api';

import { hasBothCCASchemes, isCCA2Scheme } from '../utils';

export const isTargetUnitDetailsWizardCompleted = (tuDetails: UnderlyingAgreementTargetUnitDetails) => {
  const registrationNumberCompleted =
    !!tuDetails?.companyRegistrationNumber || !tuDetails?.registrationNumberMissingReason;

  const detailsCompleted = !!tuDetails?.operatorName && !!tuDetails?.operatorType;
  const operatorAddressCompleted = !!tuDetails?.operatorAddress;
  const responsiblePersonCompleted =
    tuDetails?.responsiblePersonDetails.email &&
    tuDetails?.responsiblePersonDetails.firstName &&
    tuDetails?.responsiblePersonDetails.lastName &&
    !!tuDetails?.responsiblePersonDetails.address;

  return registrationNumberCompleted && detailsCompleted && operatorAddressCompleted && responsiblePersonCompleted;
};

export const isAdditionalEvidenceWizardCompleted = (
  authorisationAndAdditionalEvidence: AuthorisationAndAdditionalEvidence,
) => !!authorisationAndAdditionalEvidence.authorisationAttachmentIds.length;

export const isTargetCompositionStepComplete = (targetComposition?: TargetComposition): boolean => {
  if (!targetComposition) return false;

  // mandatory fields
  if (
    !targetComposition.calculatorFile ||
    !targetComposition.measurementType ||
    !targetComposition.agreementCompositionType
  )
    return false;

  const isNovem = targetComposition.agreementCompositionType === 'NOVEM';

  if (isNovem) return true;
  if (!targetComposition.isTargetUnitThroughputMeasured) return true;

  // check if novem fields are populated
  const areConditionalFieldsComplete =
    !!targetComposition.throughputUnit && !!targetComposition.conversionEvidences?.length;

  if (areConditionalFieldsComplete) return true;
  return false;
};

export const isBaselineDataStepComplete = (
  baselineData: BaselineData,
  agreementCompositionType: TargetComposition['agreementCompositionType'],
): boolean => {
  if (!baselineData) return false;

  const { isTwelveMonths, baselineDate, energy, usedReportingMechanism, throughput, energyCarbonFactor } = baselineData;

  if (typeof isTwelveMonths !== 'boolean') return false;
  if (!baselineDate) return false;

  const hasValidExplanation = explanationValid(baselineData);
  if (!hasValidExplanation) return false;

  if (!energy || !energyCarbonFactor) return false;

  if (agreementCompositionType !== 'NOVEM' && (!throughput || typeof usedReportingMechanism !== 'boolean'))
    return false;

  return true;
};

//TODO populate with business logic || validations
export const isBaselineEnergyStepComplete = (): boolean => {
  return true;
};

export const isTargetsStepComplete = (
  improvement: string,
  agreementCompositionType: TargetComposition['agreementCompositionType'],
): boolean => {
  if (!agreementCompositionType) return false;
  return typeof improvement === 'string';
};

function explanationValid(baselineData: BaselineData): boolean {
  if (!baselineData.isTwelveMonths) return !!baselineData.explanation;

  const baselineConditionDate = new Date(baselineData.baselineDate);
  const isExplanationRequired =
    baselineConditionDate && baselineConditionDate.getTime() !== new Date('2018-01-01').getTime();

  if (isExplanationRequired && !baselineData.explanation) return false;
  return true;
}

export const isTargetPeriodWizardCompleted = (targetPeriodDetails?: TargetPeriod6Details): boolean => {
  if (!targetPeriodDetails) return false;

  const targetCompositionStepComplete = isTargetCompositionStepComplete(targetPeriodDetails.targetComposition);
  const baselineDataStepComplete = isBaselineDataStepComplete(
    targetPeriodDetails.baselineData,
    targetPeriodDetails.targetComposition?.agreementCompositionType,
  );

  const targetsStepComplete = isTargetsStepComplete(
    targetPeriodDetails.targets?.improvement,
    targetPeriodDetails.targetComposition?.agreementCompositionType,
  );

  return targetCompositionStepComplete && baselineDataStepComplete && targetsStepComplete;
};

export const isCCA2FacilityWizardCompleted = (facility: Facility) => {
  return (
    !!facility &&
    !!facility?.facilityDetails &&
    !!facility?.facilityContact &&
    !!facility?.eligibilityDetailsAndAuthorisation &&
    !!facility?.facilityExtent &&
    !!facility?.apply70Rule
  );
};

export const isCCA3FacilityWizardCompleted = (facility: Facility) => {
  return (
    isCCA2FacilityWizardCompleted(facility) &&
    !!facility?.cca3BaselineAndTargets?.targetComposition &&
    !!facility?.cca3BaselineAndTargets?.baselineData &&
    !!facility?.cca3BaselineAndTargets?.facilityBaselineEnergyConsumption &&
    !!facility?.cca3BaselineAndTargets?.facilityTargets
  );
};

export const isFacilityWizardCompleted = (facility: Facility) => {
  const schemeVersions = facility?.facilityDetails?.participatingSchemeVersions;

  if (isCCA2Scheme(schemeVersions) && !hasBothCCASchemes(schemeVersions)) {
    return isCCA2FacilityWizardCompleted(facility);
  }

  return isCCA3FacilityWizardCompleted(facility);
};
