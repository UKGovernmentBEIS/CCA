import { RequestTaskState } from '@netz/common/store';

import { Facility, RequestTaskItemDTO, TargetUnitAccountDetails, UnderlyingAgreementPayload } from 'cca-api';

import {
  UNAApplicationRequestTaskPayload,
  UNAReviewRequestTaskPayload,
  UNAVariationRequestTaskPayload,
} from '../underlying-agreement.types';

export const facilities: Facility[] = [
  {
    facilityId: 'ADS_1-F00001',
    status: 'NEW',
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
    status: 'LIVE',
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

export const mockUnderlyingAgreement: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: {
    isCompanyRegistrationNumber: true,
    operatorType: 'PARTNERSHIP',
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
  },
  facilities,
  targetPeriod5Details: {
    exist: true,
    details: {
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
export const mockUnderlyingAgreementABSOLUTE: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: {
    isCompanyRegistrationNumber: true,
    operatorType: 'PARTNERSHIP',
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
  },

  facilities,
  targetPeriod5Details: {
    exist: true,
    details: {
      targetComposition: {
        calculatorFile: '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb',
        measurementType: 'ENERGY_KWH',
        agreementCompositionType: 'ABSOLUTE',
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
export const mockUnderlyingAgreementABSOLUTENoMeasurement: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: {
    isCompanyRegistrationNumber: true,
    operatorType: 'PARTNERSHIP',
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
  },

  facilities,
  targetPeriod5Details: {
    exist: true,
    details: {
      targetComposition: {
        calculatorFile: '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb',
        measurementType: 'ENERGY_KWH',
        agreementCompositionType: 'ABSOLUTE',
        isTargetUnitThroughputMeasured: false,
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
export const mockUnderlyingAgreementNOVEM: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: {
    isCompanyRegistrationNumber: true,
    operatorType: 'PARTNERSHIP',
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
  },

  facilities,
  targetPeriod5Details: {
    exist: true,
    details: {
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
  },
  targetPeriod6Details: {
    targetComposition: {
      calculatorFile: '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb',
      measurementType: 'ENERGY_KWH',
      agreementCompositionType: 'NOVEM',
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

export const mockUnaRequestTaskPayload: UNAApplicationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: mockUnderlyingAgreement,
  sectionsCompleted: {
    underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
  },
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
  },
};

export const mockUnaVariationRequestTaskPayload: UNAVariationRequestTaskPayload = {
  ...mockUnaRequestTaskPayload,
  underlyingAgreement: {
    ...mockUnderlyingAgreement,
    underlyingAgreementVariationDetails: {
      reason: 'Variation reason',
      modifications: ['AMEND_70_PERCENT_RULE_EVALUATION', 'CHANGE_THROUGHPUT_UNIT'],
    },
  },
};

export const mockUnaReviewRequestTaskPayload: UNAReviewRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: {
      operatorName: 'Flying Company 3',
      operatorType: 'LIMITED_COMPANY',
      companyRegistrationNumber: '123123123123',
      address: {
        line1: 'Address',
        line2: 'Apartment 1',
        city: 'Addres1',
        postcode: '94043',
        country: 'GB',
      },
      responsiblePerson: {
        email: 'responsible_person@cca.uk',
        firstName: 'Operator',
        lastName: 'Admin',
        address: {
          line1: 'Address',
          line2: 'Apartment 1',
          city: 'Addres1',
          postcode: '94043',
          country: 'GB',
        },
        phoneNumber: {},
      },
      administrativeContactDetails: {
        email: 'administrative_conctact@cca.uk',
        firstName: 'Operator',
        lastName: 'Admin',
        address: {
          line1: 'Address',
          line2: 'Apartment 1',
          city: 'Addres1',
          postcode: '94043',
          country: 'GB',
        },
        phoneNumber: {},
      },
    },
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_1',
      measurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: {
    underlyingAgreementTargetUnitDetails: {
      operatorName: 'Flying Company 3',
      operatorAddress: {
        line1: 'Address',
        line2: 'Apartment 1',
        city: 'Addres1',
        postcode: '94043',
        country: 'GB',
      },
      responsiblePersonDetails: {
        firstName: 'Operator',
        lastName: 'Admin',
        email: 'responsible_person@cca.uk',
        address: {
          line1: 'Address',
          line2: 'Apartment 1',
          city: 'Addres1',
          postcode: '94043',
          country: 'GB',
        },
      },
      operatorType: 'LIMITED_COMPANY',
      isCompanyRegistrationNumber: true,
      companyRegistrationNumber: '123123123123',
      subsectorAssociationName: 'SUBSECTOR_1',
    },
    facilities: [
      {
        facilityId: 'ADS_1-F00001',
        status: 'NEW',
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
        status: 'NEW',
        facilityId: 'ADS_53-F00007',
        facilityDetails: {
          name: 'Facility 1',
          isCoveredByUkets: false,
          applicationReason: 'NEW_AGREEMENT',
          facilityAddress: {
            line1: 'Address',
            line2: 'Apartment 1',
            city: 'Addres1',
            postcode: '94043',
            country: 'GB',
          },
        },
        facilityContact: {
          email: 'administrative_conctact@cca.uk',
          firstName: 'Operator',
          lastName: 'Admin',
          address: {
            line1: 'Address',
            line2: 'Apartment 1',
            city: 'Addres1',
            postcode: '94043',
            country: 'GB',
          },
        },
        eligibilityDetailsAndAuthorisation: {
          isConnectedToExistingFacility: false,
          agreementType: 'ENERGY_INTENSIVE',
        },
        facilityExtent: {
          manufacturingProcessFile: '91b58e9a-562a-42eb-b9a8-cd81071d9e70',
          processFlowFile: '27ff5b7e-3334-4a7c-af7e-0f4bd3096dc9',
          annotatedSitePlansFile: 'f9bdb167-97f8-4b25-99a7-dbee13d44e77',
          eligibleProcessFile: 'e060bfb2-deac-40be-9337-0c88d860a801',
          areActivitiesClaimed: false,
        },
        apply70Rule: {
          energyConsumed: 100,
          energyConsumedEligible: 100,
          evidenceFile: '21d97034-7958-419e-a655-8bc1a2c8f12a',
        },
      },
    ],
    targetPeriod5Details: {
      exist: false,
    },
    targetPeriod6Details: {
      targetComposition: {
        calculatorFile: '1acfa056-39f1-415b-b69a-3c771db1c4df',
        measurementType: 'ENERGY_MWH',
        agreementCompositionType: 'ABSOLUTE',
        throughputUnit: 'tonnne',
        conversionEvidences: [],
      },
      baselineData: {
        isTwelveMonths: false,
        baselineDate: '2023-02-01',
        explanation: 'xfdasdασδασδsd',
        greenfieldEvidences: [],
        energy: 3022,
        usedReportingMechanism: false,
        throughput: 100,
        energyCarbonFactor: 100,
      },
      targets: {
        improvement: 100,
        target: 0,
      },
    },
    authorisationAndAdditionalEvidence: {
      authorisationAttachmentIds: ['6a8ab09d-531e-4529-893e-6e0fac29a13b'],
      additionalEvidenceAttachmentIds: [],
    },
  },
  sectionsCompleted: {
    'ADS_53-F00006': 'COMPLETED',
    'ADS_53-F00007': 'COMPLETED',
    manageFacilities: 'COMPLETED',
    targetPeriod5Details: 'COMPLETED',
    targetPeriod6Details: 'COMPLETED',
    authorisationAndAdditionalEvidence: 'COMPLETED',
    underlyingAgreementTargetUnitDetails: 'COMPLETED',
  },
  underlyingAgreementAttachments: {
    '1acfa056-39f1-415b-b69a-3c771db1c4df': 'file_example_XLSX_50.xlsx',
    '21d97034-7958-419e-a655-8bc1a2c8f12a': 'file_example_XLSX_50.xlsx',
    '27ff5b7e-3334-4a7c-af7e-0f4bd3096dc9': 'file_example_XLSX_50.xlsx',
    '4e9053f2-b1ab-4f5f-968b-9b40656178ae': 'file_example_XLSX_50.xlsx',
    '514fe102-fa6e-4085-9321-3d3803bbc957': 'file_example_XLSX_50.xlsx',
    '6a8ab09d-531e-4529-893e-6e0fac29a13b': 'file_example_XLSX_50.xlsx',
    '91b58e9a-562a-42eb-b9a8-cd81071d9e70': 'file_example_XLSX_50.xlsx',
    'ab3fdeed-122e-4518-acd3-dc6ba16fb437': 'file_example_XLSX_50.xlsx',
    'df6ebb3b-eecf-4fdb-a1b6-a72e15c1f53a': 'file_example_XLSX_50.xlsx',
    'e060bfb2-deac-40be-9337-0c88d860a801': 'file_example_XLSX_50.xlsx',
    'f5ebeac3-b9c0-42c7-a8ac-ce04aa4a7bf2': 'file_example_XLSX_50.xlsx',
    'f9bdb167-97f8-4b25-99a7-dbee13d44e77': 'file_example_XLSX_50.xlsx',
  },
  reviewSectionsCompleted: {
    'ADS_53-F00006': 'REJECTED',
    'ADS_53-F00007': 'REJECTED',
    targetPeriod5Details: 'APPROVED',
    targetPeriod6Details: 'APPROVED',
    authorisationAndAdditionalEvidence: 'APPROVED',
    underlyingAgreementTargetUnitDetails: 'APPROVED',
    overallDecision: 'REJECTED',
  },
  reviewGroupDecisions: {
    TARGET_UNIT_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'asd',
      },
    },
    TARGET_PERIOD5_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'asd',
      },
    },
    TARGET_PERIOD6_DETAILS: {
      type: 'ACCEPTED',
      details: {
        notes: 'asd',
      },
    },
    AUTHORISATION_AND_ADDITIONAL_EVIDENCE: {
      type: 'ACCEPTED',
      details: {
        notes: 'asdasd',
      },
    },
  },
  facilitiesReviewGroupDecisions: {
    'ADS_53-F00006': {
      type: 'REJECTED',
      details: {
        notes: 'asd',
      },
    },
    'ADS_53-F00007': {
      type: 'REJECTED',
      details: {
        notes: 'asd',
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
export const mockUnaRequestTaskPayloadNOVEM: UNAApplicationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: mockUnderlyingAgreementNOVEM,
  sectionsCompleted: {
    underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
  },
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
  },
};
export const mockUnaRequestTaskPayloadABSOLUTE: UNAApplicationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
      throughputUnit: 'tonne',
    },
  },
  underlyingAgreement: mockUnderlyingAgreementABSOLUTE,
  sectionsCompleted: {
    underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
  },
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
  },
};
export const mockUnaRequestTaskPayloadABSOLUTENoMeasurement: UNAApplicationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: mockUnderlyingAgreementABSOLUTE,
  sectionsCompleted: {
    underlyingAgreementTargetUnitDetails: 'IN_PROGRESS',
  },
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
  },
};
export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT',
    payload: mockUnaRequestTaskPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
    'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_CANCEL_APPLICATION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
    },
  },
};
export const mockVariationRequestTaskItemDTO: RequestTaskItemDTO = {
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
export const mockRequestTaskItemUNAReviewDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 9,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW',
    assignable: true,
    assigneeUserId: '1c6dcab2-521b-4ddc-b6f3-71079d6555fa',
    assigneeFullName: 'Regulator Admin',
    startDate: '2024-09-16T11:31:42.327727Z',
    payload: mockUnaReviewRequestTaskPayload,
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW',
    'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION',
    'UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION',
    'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION',
    'UNDERLYING_AGREEMENT_CANCEL_APPLICATION',
    'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION',
  ],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_53-T00003-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 3,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
    },
    paymentCompleted: true,
    paymentAmount: '0',
  },
};
export const mockRequestTaskItemDTONOVEM: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT',
    payload: mockUnaRequestTaskPayloadNOVEM,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
    'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_CANCEL_APPLICATION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
    },
  },
};

export const mockRequestTaskItemDTOABSOLUTE: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT',
    payload: mockUnaRequestTaskPayloadABSOLUTE,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
    'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_CANCEL_APPLICATION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
    },
  },
};
export const mockRequestTaskItemDTOABSOLUTENoMeasurement: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT',
    payload: mockUnaRequestTaskPayloadABSOLUTE,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_SUBMIT_APPLICATION',
    'UNDERLYING_AGREEMENT_UPLOAD_SECTION_ATTACHMENT',
    'UNDERLYING_AGREEMENT_CANCEL_APPLICATION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
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
export const mockUNAReviewRequestTaskState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemUNAReviewDTO,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
