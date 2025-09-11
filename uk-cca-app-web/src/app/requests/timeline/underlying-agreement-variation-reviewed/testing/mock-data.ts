import { RequestActionState } from '@netz/common/store';
import { mockTargetUnitDetails, UnderlyingAgreementVariationDecisionRequestActionPayload } from '@requests/common';

import {
  DefaultNoticeRecipient,
  Determination,
  Facility,
  RequestActionDTO,
  UnderlyingAgreementReviewDecision,
  UnderlyingAgreementVariationFacilityReviewDecision,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

const operatorAddress = {
  line1: 'test',
  city: 'test',
  postcode: 'test',
  county: 'test',
  country: 'GR',
};

const facilities: Array<Facility> = [
  {
    status: 'LIVE',
    facilityId: 'ADS_2-F00028',
    facilityDetails: {
      name: 'Facility 2-2',
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
      facilityAddress: operatorAddress,
    },
    facilityContact: {
      email: 'test29@test.com',
      firstName: 'firstName29',
      lastName: 'lastName29',
      address: operatorAddress,
      phoneNumber: {
        countryCode: '30',
        number: '6999999999',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: false,
      agreementType: 'ENERGY_INTENSIVE',
    },
    facilityExtent: {
      manufacturingProcessFile: '7498a395-21c7-4093-a283-eb812c981e7a',
      processFlowFile: '6ad70b10-19cc-40b7-82a4-6bed38097ea7',
      annotatedSitePlansFile: '83f928ce-0caf-412f-834f-bc263c0f83ef',
      eligibleProcessFile: 'd051700b-467c-45a5-94b5-81440dd19f12',
      areActivitiesClaimed: false,
    },
    apply70Rule: {
      energyConsumed: 75,
      energyConsumedEligible: 100,
      evidenceFile: '3ca25915-bbb5-4f03-9ecf-3a8ad583443e',
    },
  },
  {
    status: 'NEW',
    facilityId: 'ADS_2-F00029',
    facilityDetails: {
      name: 'Facility 1-2',
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
      facilityAddress: operatorAddress,
    },
    facilityContact: {
      email: 'test29@test.com',
      firstName: 'firstName29',
      lastName: 'lastName29',
      address: operatorAddress,
      phoneNumber: {
        countryCode: '30',
        number: '6999999999',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: false,
      agreementType: 'ENERGY_INTENSIVE',
    },
    facilityExtent: {
      manufacturingProcessFile: '7498a395-21c7-4093-a283-eb812c981e7a',
      processFlowFile: '6ad70b10-19cc-40b7-82a4-6bed38097ea7',
      annotatedSitePlansFile: '83f928ce-0caf-412f-834f-bc263c0f83ef',
      eligibleProcessFile: 'd051700b-467c-45a5-94b5-81440dd19f12',
      areActivitiesClaimed: false,
    },
    apply70Rule: {
      energyConsumed: 75,
      energyConsumedEligible: 100,
      evidenceFile: '3ca25915-bbb5-4f03-9ecf-3a8ad583443e',
    },
  },
];

const underlyingAgreement: UnderlyingAgreementVariationPayload = {
  underlyingAgreementVariationDetails: {
    reason: 'Variation reason',
    modifications: ['AMEND_70_PERCENT_RULE_EVALUATION', 'CHANGE_THROUGHPUT_UNIT'],
  },
  underlyingAgreementTargetUnitDetails: {
    operatorName: 'Target Unit 22',
    operatorAddress: operatorAddress,
    responsiblePersonDetails: {
      firstName: 'test',
      lastName: 'test',
      email: 'test@test.com',
      address: operatorAddress,
    },
    operatorType: 'LIMITED_COMPANY',
    isCompanyRegistrationNumber: true,
    companyRegistrationNumber: 'test1234',
  },
  facilities: facilities,
  targetPeriod5Details: {
    exist: false,
  },
  targetPeriod6Details: {
    targetComposition: {
      calculatorFile: 'b8c208df-4405-42ce-b7ca-7e152eb771e5',
      measurementType: 'ENERGY_MWH',
      agreementCompositionType: 'RELATIVE',
      throughputUnit: 'kg',
      conversionEvidences: [],
    },
    baselineData: {
      isTwelveMonths: true,
      baselineDate: '2020-02-02',
      explanation: 'test',
      greenfieldEvidences: [],
      energy: 1,
      usedReportingMechanism: true,
      throughput: 1,
      performance: 1,
      energyCarbonFactor: 1,
    },
    targets: {
      improvement: 1,
      target: 0.99,
    },
  },
  authorisationAndAdditionalEvidence: {
    authorisationAttachmentIds: ['aeb9765c-2d23-4134-a406-50758e8986bb'],
    additionalEvidenceAttachmentIds: [],
  },
};

const underlyingAgreementAttachments = {
  '3ca25915-bbb5-4f03-9ecf-3a8ad583443e':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092642100.xlsx',
  '6ad70b10-19cc-40b7-82a4-6bed38097ea7':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2).xlsx',
  '7498a395-21c7-4093-a283-eb812c981e7a':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2) (1).xlsx',
  '83f928ce-0caf-412f-834f-bc263c0f83ef':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2) (1).xlsx',
  'aeb9765c-2d23-4134-a406-50758e8986bb':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2) (2).xlsx',
  'b8c208df-4405-42ce-b7ca-7e152eb771e5':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2) (1).xlsx',
  'd051700b-467c-45a5-94b5-81440dd19f12':
    'uk_ets_Standard_Report_Verified_Emissions_Surrendered_Allowances_20230831092155510 (2) (1).xlsx',
};

const decisionNotification = {
  signatory: '852901e2-65a1-4e6f-b43b-9c53c1a2ef73',
};

const defaultContacts: Array<DefaultNoticeRecipient> = [
  {
    name: 'test test',
    email: 'test@test.com',
    recipientType: 'RESPONSIBLE_PERSON',
  },
  {
    name: 'test test',
    email: 'test@test.com',
    recipientType: 'ADMINISTRATIVE_CONTACT',
  },
  {
    name: 'Fred_2 William_2',
    email: 'fredwilliam_2@agindustries.org.uk',
    recipientType: 'SECTOR_CONTACT',
  },
];

const usersInfo = {
  '852901e2-65a1-4e6f-b43b-9c53c1a2ef73': {
    name: 'Regulator 2',
  },
};

const officialNotice = {
  name: 'Underlying agreement rejection notice.pdf',
  uuid: 'ad2d77a4-9882-4bcd-bfc1-5f7818a527aa',
};

const determination: Determination = {
  type: 'REJECTED',
  reason: 'My rejected reason',
  additionalInformation: 'My info',
  files: [],
};

const reviewSectionsCompleted = {
  'ADS_2-F00028': 'ACCEPTED',
  'ADS_2-F00029': 'ACCEPTED',
  underlyingAgreementVariationDetails: 'ACCEPTED',
  targetPeriod5Details: 'ACCEPTED',
  targetPeriod6Details: 'ACCEPTED',
  authorisationAndAdditionalEvidence: 'ACCEPTED',
  underlyingAgreementTargetUnitDetails: 'ACCEPTED',
};

const reviewGroupDecisions: Record<string, UnderlyingAgreementReviewDecision> = {
  VARIATION_DETAILS: {
    type: 'ACCEPTED',
    details: {
      notes: 'test',
    },
  },
  TARGET_UNIT_DETAILS: {
    type: 'ACCEPTED',
    details: {
      notes: 'test',
    },
  },
  TARGET_PERIOD5_DETAILS: {
    type: 'ACCEPTED',
    details: {
      notes: 'ok',
    },
  },
  TARGET_PERIOD6_DETAILS: {
    type: 'ACCEPTED',
    details: {
      notes: 'ok',
    },
  },
  AUTHORISATION_AND_ADDITIONAL_EVIDENCE: {
    type: 'ACCEPTED',
    details: {
      notes: 'ok',
    },
  },
};

const facilitiesReviewGroupDecisions: Record<string, UnderlyingAgreementVariationFacilityReviewDecision> = {
  'ADS_2-F00028': {
    type: 'ACCEPTED',
    details: {
      notes: 'ss',
    },
    facilityStatus: 'LIVE',
  },
  'ADS_2-F00029': {
    type: 'ACCEPTED',
    details: {
      notes: 'ss',
    },
    changeStartDate: false,
    facilityStatus: 'NEW',
  },
};

const underlyingAgreementVariationPayload: UnderlyingAgreementVariationDecisionRequestActionPayload = {
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      schemeDataMap: {
        ['CCA_2']: { sectorMeasurementType: 'ENERGY_KWH' },
      },
    },
  },
  businessId: '64',
  underlyingAgreement: underlyingAgreement,
  underlyingAgreementAttachments: underlyingAgreementAttachments,
  decisionNotification: decisionNotification,
  defaultContacts: defaultContacts,
  usersInfo: usersInfo,
  officialNotice: officialNotice,
  determination: determination,
  reviewSectionsCompleted: reviewSectionsCompleted,
  reviewGroupDecisions: reviewGroupDecisions,
  facilitiesReviewGroupDecisions: facilitiesReviewGroupDecisions,
  reviewAttachments: {},
};

const mockAcceptedRequestActionDTO: RequestActionDTO = {
  id: 64,
  type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED',
  payload: {
    ...underlyingAgreementVariationPayload,
    determination: {
      type: 'ACCEPTED',
      additionalInformation: 'My info',
      files: [],
    },
    underlyingAgreementDocument: {
      name: 'underlying-agreement.pdf',
      uuid: 'abcd-1234',
    },
  } as UnderlyingAgreementVariationDecisionRequestActionPayload,
  requestId: 'ADS_2-T00026-UNA',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 45,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator 2',
  creationDate: '2024-09-25T18:21:32.751217Z',
};

const mockRejectedRequestActionDTO: RequestActionDTO = {
  id: 64,
  type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED',
  payload: underlyingAgreementVariationPayload,
  requestId: 'ADS_2-T00026-VAR-1',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 45,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator 2',
  creationDate: '2024-09-25T18:21:32.751217Z',
};

export const mockAcceptedRequestActionState: RequestActionState = {
  action: mockAcceptedRequestActionDTO,
};

export const mockRejectedRequestActionState: RequestActionState = {
  action: mockRejectedRequestActionDTO,
};
