import { RequestTaskState } from '@netz/common/store';
import { UNAVariationRequestTaskPayload } from '@requests/common';

import {
  Facility,
  RequestTaskItemDTO,
  TargetUnitAccountDetails,
  UnderlyingAgreementContainer,
  UnderlyingAgreementPayload,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

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
    status: 'NEW',
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
  {
    facilityId: 'ADS_1-F00003',
    status: 'EXCLUDED',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Facility 3',
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
    facilityId: 'ADS_1-F00004',
    status: 'NEW',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Facility 4',
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

const facilitiesOriginal: Facility[] = [
  {
    facilityId: 'ADS_1-F00001',
    status: 'NEW',
    facilityDetails: {
      name: 'Original Facility 1',
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
    status: 'NEW',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Original Facility 2',
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
    facilityId: 'ADS_1-F00003',
    status: 'LIVE',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Original Facility 3',
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
    facilityId: 'ADS_1-F00004',
    status: 'EXCLUDED',
    excludedDate: '2021-03-29',
    facilityDetails: {
      name: 'Original Facility 4',
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

export const mockUnderlyingAgreementVariation: UnderlyingAgreementVariationPayload = {
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
    companyRegistrationNumber: '11112222',
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

export const mockUnderlyingAgreementVariationOriginal: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: {
    operatorName: 'Green Energy Solutions Ltd',
    operatorAddress: {
      line1: '45 Industrial Park Way',
      city: 'Manchester',
      postcode: 'M15 6BN',
      country: 'United Kingdom',
    },
    responsiblePersonDetails: {
      firstName: 'Sarah',
      lastName: 'Johnson',
      email: 'sarah.johnson@greenenergy.com',
      address: {
        line1: '12 Oak Avenue',
        line2: 'Suite 300',
        city: 'Manchester',
        postcode: 'M14 5WD',
        county: 'Greater Manchester',
        country: 'United Kingdom',
      },
    },
    operatorType: 'LIMITED_COMPANY',
    isCompanyRegistrationNumber: true,
    companyRegistrationNumber: '08745123',
  },
  facilities: facilitiesOriginal,
  targetPeriod5Details: {
    exist: false,
  },
  targetPeriod6Details: {
    targetComposition: {
      calculatorFile: '7d9e456f-2211-4abc-8d5e-12a34b567c89',
      measurementType: 'ENERGY_KWH',
      agreementCompositionType: 'RELATIVE',
      isTargetUnitThroughputMeasured: true,
      throughputUnit: 'MWh',
      conversionFactor: 3.6,
      conversionEvidences: ['9a8b7c6d-5e4f-3g2h-1i0j-k9l8m7n6o5p4'],
    },
    baselineData: {
      isTwelveMonths: true,
      baselineDate: '2023-06-30T00:00:00.000Z',
      explanation: 'Updated baseline due to facility upgrades and new equipment installation',
      greenfieldEvidences: ['123e4567-e89b-12d3-a456-426614174000'],
      energy: 250000,
      usedReportingMechanism: true,
      throughput: 75000,
      energyCarbonFactor: 0.233,
      performance: 3.33,
    },
    targets: {
      improvement: 0.15,
      target: 2.83,
    },
  },
  authorisationAndAdditionalEvidence: {
    authorisationAttachmentIds: ['5a4b3c2d-1e2f-3g4h-5i6j-7k8l9m0n1o2p'],
    additionalEvidenceAttachmentIds: ['abcd1234-5678-90ef-ghij-klmnopqrstuv'],
  },
};

export const mockTargetUnitDetails: TargetUnitAccountDetails = {
  operatorName: 'test',
  operatorType: 'LIMITED_COMPANY',
  companyRegistrationNumber: 'test1234',
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

export const mockOriginalUnderlyingAgreementContainer: UnderlyingAgreementContainer = {
  schemeDataMap: {
    ['CCA_2']: { sectorMeasurementType: 'ENERGY_KWH' },
  },
  underlyingAgreement: mockUnderlyingAgreementVariationOriginal,
  underlyingAgreementAttachments: {
    '061b1268-57ac-4b16-890f-36211d37686a': 'sample_profile.bmp',
    '3fec47fe-f7c0-4d88-af52-bae6a38442cd': 'sample_profile1.png',
    '7d9e456f-2211-4abc-8d5e-12a34b567c89': 'target_calculator_original.xlsx',
    '9a8b7c6d-5e4f-3g2h-1i0j-k9l8m7n6o5p4': 'conversion_evidence_original.pdf',
    '123e4567-e89b-12d3-a456-426614174000': 'greenfield_evidence_original.pdf',
    '5a4b3c2d-1e2f-3g4h-5i6j-7k8l9m0n1o2p': 'authorisation_original.pdf',
    'abcd1234-5678-90ef-ghij-klmnopqrstuv': 'additional_evidence_original.pdf',
  },
};

export const mockUnaVariationRequestTaskPayload: UNAVariationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      schemeDataMap: {
        ['CCA_2']: { sectorMeasurementType: 'ENERGY_KWH' },
      },
    },
  },
  underlyingAgreement: mockUnderlyingAgreementVariation,
  sectionsCompleted: {
    underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
  },
  underlyingAgreementAttachments: {
    '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb': 'target_calculator_tp6.xlsx',
    '1b2af8ce-4311-4a8c-9a2c-03d77b5a8fbb': 'conversion_evidence_tp6.pdf',
    'f8dff40f-3fb7-4368-8723-244661fb686f': 'greenfield_evidence_tp6.pdf',
    '2f611a22-bd5f-4fd4-b00f-c20bfb2706c9': 'authorisation_document.pdf',
  },
  originalUnderlyingAgreementContainer: mockOriginalUnderlyingAgreementContainer,
};

export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT',
    payload: mockUnaVariationRequestTaskPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_VARIATION_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
    'UNDERLYING_AGREEMENT_VARIATION_CANCEL_APPLICATION',
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

export const mockRequestTaskState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTO,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
