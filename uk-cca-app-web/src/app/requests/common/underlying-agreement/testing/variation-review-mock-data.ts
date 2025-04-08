import { RequestTaskState } from '@netz/common/store';
import { UNAVariationReviewRequestTaskPayload } from '@requests/common';

import { Facility, RequestTaskItemDTO, TargetUnitAccountDetails, UnderlyingAgreementVariationPayload } from 'cca-api';

const facilities: Facility[] = [
  {
    facilityId: 'ADS_1-F00001',
    status: 'LIVE',
    facilityDetails: {
      name: 'Facility 1',
      facilityAddress: {
        city: 'Facility City',
        line1: 'Facility Line1',
        line2: 'Facility Line2',
        country: 'GR',
        postcode: 'Facility 14',
      },
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
    },
    facilityContact: {
      firstName: 'FacilityFirst',
      lastName: 'FacilityLast',
      email: 'facility@email.com',
      address: {
        city: 'Facility Contact City',
        line1: 'Facility Contact Line1',
        line2: 'Facility Contact Line2',
        country: 'GR',
        postcode: 'Facility Contact 14',
      },
      phoneNumber: {
        countryCode: '44',
        number: '1234567890',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: true,
      adjacentFacilityId: 'ADS_1-F11111',
      agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      erpAuthorisationExists: true,
      authorisationNumber: 'authorisation',
      regulatorName: 'ENVIRONMENT_AGENCY',
      permitFile: 'permitFile',
    },
    facilityExtent: {
      manufacturingProcessFile: 'manufacturingProcessFile',
      processFlowFile: 'processFlowFile',
      annotatedSitePlansFile: 'annotatedSitePlansFile',
      eligibleProcessFile: 'eligibleProcessFile',
      areActivitiesClaimed: true,
      activitiesDescriptionFile: 'activitiesDescriptionFile',
    },
    apply70Rule: {
      energyConsumed: 50,
      energyConsumedProvision: 40,
      energyConsumedEligible: 70,
      evidenceFile: 'evidenceFile',
    },
  },
  {
    facilityId: 'ADS_1-F00002',
    status: 'EXCLUDED',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Facility 2',
      facilityAddress: {
        city: 'Facility City',
        line1: 'Facility Line1',
        line2: 'Facility Line2',
        country: 'GR',
        postcode: 'Facility 14',
      },
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
    },
    facilityContact: {
      firstName: 'FacilityFirst',
      lastName: 'FacilityLast',
      email: 'facility@email.com',
      address: {
        city: 'Facility Contact City',
        line1: 'Facility Contact Line1',
        line2: 'Facility Contact Line2',
        country: 'GR',
        postcode: 'Facility Contact 14',
      },
      phoneNumber: {
        countryCode: '44',
        number: '1234567890',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: true,
      adjacentFacilityId: 'ADS_1-F11111',
      agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
      erpAuthorisationExists: true,
      authorisationNumber: 'authorisation',
      regulatorName: 'ENVIRONMENT_AGENCY',
      permitFile: 'permitFile',
    },
    facilityExtent: {
      manufacturingProcessFile: 'manufacturingProcessFile',
      processFlowFile: 'processFlowFile',
      annotatedSitePlansFile: 'annotatedSitePlansFile',
      eligibleProcessFile: 'eligibleProcessFile',
      areActivitiesClaimed: true,
      activitiesDescriptionFile: 'activitiesDescriptionFile',
    },
    apply70Rule: {
      energyConsumed: 50,
      energyConsumedProvision: 40,
      energyConsumedEligible: 70,
      evidenceFile: 'evidenceFile',
    },
  },
];

export const mockUnderlyingAgreementVariationReview: UnderlyingAgreementVariationPayload = {
  underlyingAgreementVariationDetails: {
    reason: 'Variation reason',
    modifications: ['AMEND_70_PERCENT_RULE_EVALUATION', 'CHANGE_THROUGHPUT_UNIT'],
  },
  underlyingAgreementTargetUnitDetails: {
    operatorName: 'operator name',
    operatorAddress: {
      line1: 'una operator addr line 1',
      city: 'una operator addr city',
      postcode: 'una operator addr postcode',
      country: 'una operator addr country',
    },
    responsiblePersonDetails: {
      firstName: 'test 1',
      lastName: 'test 2',
      email: 'test@test.com',
      address: {
        line1: 'una resp addr line 1',
        line2: 'una resp addr line 2',
        city: 'una resp addr line city',
        postcode: 'una resp addr line postcode',
        county: 'una resp addr county',
        country: 'una resp addr country',
      },
    },
    operatorType: 'LIMITED_COMPANY',
    isCompanyRegistrationNumber: true,
    companyRegistrationNumber: '1111',
  },
  facilities,
  targetPeriod5Details: {
    exist: false,
  },
  targetPeriod6Details: {
    targetComposition: {
      calculatorFile: '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb',
      measurementType: 'ENERGY_KWH',
      agreementCompositionType: 'RELATIVE',
      isTargetUnitThroughputMeasured: true,
      throughputUnit: 'GJ',
      conversionFactor: 1,
      conversionEvidences: ['1b2af8ce-4311-4a8c-9a2c-03d77b5a8fbb'],
    },
    baselineData: {
      isTwelveMonths: false,
      baselineDate: '2020-12-12T00:00:00.000Z',
      explanation: 'test',
      greenfieldEvidences: ['f8dff40f-3fb7-4368-8723-244661fb686f'],
      energy: 100,
      usedReportingMechanism: true,
      throughput: 10,
      energyCarbonFactor: 1,
      performance: 10,
    },
    targets: {
      improvement: 0.1,
      target: 9,
    },
  },
  authorisationAndAdditionalEvidence: {
    authorisationAttachmentIds: ['2f611a22-bd5f-4fd4-b00f-c20bfb2706c9'],
    additionalEvidenceAttachmentIds: [],
  },
};

export const mockTargetUnitDetails: TargetUnitAccountDetails = {
  operatorName: 'test',
  operatorType: 'LIMITED_COMPANY',
  companyRegistrationNumber: 'test',
  address: {
    line1: 'accountrefdata operator address line 1',
    line2: 'accountrefdata operator address line 2',
    city: 'accountrefdata operator address city',
    postcode: 'accountrefdata operator address postcode',
    county: 'accountrefdata operator address county',
    country: 'accountrefdata operator address country',
  },
  responsiblePerson: {
    email: 'test@test.com',
    firstName: 'resp person firstname',
    lastName: 'resp person lastname',
    jobTitle: 'resp person jobtitle',
    address: {
      line1: 'accountrefdata resp pers line 1',
      line2: 'accountrefdata resp pers line 2',
      city: 'accountrefdata resp pers city',
      postcode: 'accountrefdata resp postcode',
      county: 'accountrefdata resp pers county',
      country: 'accountrefdata resp pers country',
    },
    phoneNumber: {
      countryCode: '30',
      number: '6999999999',
    },
  },
  administrativeContactDetails: {
    email: 'test@test.com',
    firstName: 'test',
    lastName: 'test',
    jobTitle: 'test',
    address: {
      line1: 'test',
      line2: 'test',
      city: 'test',
      postcode: 'test',
      county: 'test',
      country: 'GR',
    },
    phoneNumber: {
      countryCode: '30',
      number: '6999999999',
    },
  },
};

export const mockUNAVariationReviewRequestTaskPayload: UNAVariationReviewRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: mockUnderlyingAgreementVariationReview,
  originalUnderlyingAgreementContainer: {
    sectorMeasurementType: 'ENERGY_KWH',
    underlyingAgreement: mockUnderlyingAgreementVariationReview,
    sectionsCompleted: {
      'ADS_1-F00001': 'COMPLETED',
      'ADS_1-F00002': 'COMPLETED',
      underlyingAgreementVariationDetails: 'COMPLETED',
      manageFacilities: 'COMPLETED',
      targetPeriod5Details: 'COMPLETED',
      targetPeriod6Details: 'COMPLETED',
      authorisationAndAdditionalEvidence: 'COMPLETED',
      underlyingAgreementTargetUnitDetails: 'COMPLETED',
    },
    sectorThroughputUnit: 'kWh',
    underlyingAgreementAttachments: {
      manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
      processFlowFile: 'processFlowFile.xlsx',
      annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
      eligibleProcessFile: 'eligibleProcessFile.xlsx',
      activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
      evidenceFile: 'evidenceFile.xlsx',
    },
  },
  sectionsCompleted: {
    'ADS_1-F00001': 'COMPLETED',
    'ADS_1-F00002': 'COMPLETED',
    underlyingAgreementVariationDetails: 'COMPLETED',
    manageFacilities: 'COMPLETED',
    targetPeriod5Details: 'COMPLETED',
    targetPeriod6Details: 'COMPLETED',
    authorisationAndAdditionalEvidence: 'COMPLETED',
    underlyingAgreementTargetUnitDetails: 'COMPLETED',
  },
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
  },
  reviewSectionsCompleted: {
    'ADS_1-F00001': 'APPROVED',
    'ADS_1-F00002': 'REJECTED',
    underlyingAgreementVariationDetails: 'APPROVED',
    targetPeriod5Details: 'APPROVED',
    targetPeriod6Details: 'APPROVED',
    authorisationAndAdditionalEvidence: 'APPROVED',
    underlyingAgreementTargetUnitDetails: 'APPROVED',
    overallDecision: 'APPROVED',
  },
  reviewGroupDecisions: {
    VARIATION_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'Variation details notes',
      },
    },
    TARGET_UNIT_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'Target unit notes',
      },
    },
    TARGET_PERIOD5_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'Tp5 notes',
      },
    },
    TARGET_PERIOD6_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'Tp6 notes',
      },
    },
    AUTHORISATION_AND_ADDITIONAL_EVIDENCE: {
      type: 'ACCEPTED',
      details: {
        notes: 'Authorisation notes',
      },
    },
  },
  facilitiesReviewGroupDecisions: {
    'ADS_1-F00001': {
      type: 'ACCEPTED',
      facilityStatus: 'LIVE',
      details: {
        notes: 'ADS_1-F00001 notes',
      },
    },
    'ADS_1-F00002': {
      type: 'REJECTED',
      facilityStatus: 'EXCLUDED',
      details: {
        notes: 'ADS_1-F00002 notes',
      },
    },
  },
  reviewAttachments: {},
  determination: {
    type: 'REJECTED',
    reason: 'asdasdasdasdasd',
    additionalInformation: 'adqwdasd',
    files: [],
  },
};

export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW',
    payload: mockUNAVariationReviewRequestTaskPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW',
    'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION',
    'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION',
    'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION',
    'UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT_VARIATION',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT_VARIATION',
    },
  },
};

export const mockVariationReviewRequestTaskState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTO,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
