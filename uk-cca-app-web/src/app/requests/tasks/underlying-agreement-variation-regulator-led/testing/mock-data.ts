import { RequestTaskState } from '@netz/common/store';
import { UNAVariationRegulatorLedRequestTaskPayload } from '@requests/common';

import {
  Facility,
  RequestTaskItemDTO,
  TargetPeriod6Details,
  TargetUnitAccountDetails,
  UnderlyingAgreementContainer,
  UnderlyingAgreementVariationDetails,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

const facilities: Facility[] = [
  {
    status: 'LIVE',
    facilityId: 'ADS_2-F00003',
    facilityDetails: {
      name: 'fac3-1-1',
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
      participatingSchemeVersions: ['CCA_3'],
      facilityAddress: {
        line1: 'addr1',
        city: 'kjhn',
        postcode: '87678',
        country: 'GB-NIR',
      },
    },
    facilityContact: {
      email: 'administr1@cca.uk',
      firstName: 'administr1',
      lastName: 'user',
      address: {
        line1: 'addr1',
        city: 'kjhn',
        postcode: '87678',
        country: 'GB-NIR',
      },
      phoneNumber: {
        countryCode: '44',
        number: '1234567890',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: false,
      agreementType: 'ENERGY_INTENSIVE',
    },
    facilityExtent: {
      manufacturingProcessFile: '47179440-0990-4a4e-9565-fc508c0af648',
      processFlowFile: '4060eeb3-87ab-4be1-93f3-c506c66cca71',
      annotatedSitePlansFile: '5fa23bf5-330f-4691-94dd-82429e2686a1',
      eligibleProcessFile: '108118e5-d9d7-49a7-a4b6-bc3be6f0e711',
      areActivitiesClaimed: false,
    },
    apply70Rule: {
      energyConsumed: '70',
      energyConsumedEligible: '100',
      evidenceFile: '32801f8a-060e-4a4f-90fc-b85175b9e637',
    },
    cca3BaselineAndTargets: {
      targetComposition: {
        calculatorFile: '099b0b27-3a3c-4f9e-af2e-661804f617b0',
        measurementType: 'ENERGY_KWH',
        agreementCompositionType: 'NOVEM',
      },
      baselineData: {
        isTwelveMonths: true,
        baselineDate: '2022-01-01',
        greenfieldEvidences: [],
        usedReportingMechanism: true,
        energyCarbonFactor: '545',
      },
      facilityBaselineEnergyConsumption: {
        totalFixedEnergy: '150',
        hasVariableEnergy: true,
        variableEnergyType: 'BY_PRODUCT',
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'prod1',
            baselineYear: 2024,
            productStatus: 'LIVE',
            energy: '234',
            throughput: '65',
            throughputUnit: 'unit',
          },
          {
            productName: 'prod2',
            baselineYear: 2022,
            productStatus: 'LIVE',
            energy: '234',
            throughput: '68',
            throughputUnit: 'unit',
          },
        ],
      },
      facilityTargets: {
        improvements: {
          TP7: '21',
          TP8: '30',
          TP9: '40',
        },
      },
    },
  },
];

const facilitiesOriginal: Facility[] = [
  {
    status: 'LIVE',
    facilityId: 'ADS_2-F00003',
    facilityDetails: {
      name: 'fac3-1-1',
      isCoveredByUkets: false,
      applicationReason: 'NEW_AGREEMENT',
      participatingSchemeVersions: ['CCA_3'],
      facilityAddress: {
        line1: 'addr1',
        city: 'kjhn',
        postcode: '87678',
        country: 'GB-NIR',
      },
    },
    facilityContact: {
      email: 'administr1@cca.uk',
      firstName: 'administr1',
      lastName: 'user',
      address: {
        line1: 'addr1',
        city: 'kjhn',
        postcode: '87678',
        country: 'GB-NIR',
      },
      phoneNumber: {
        countryCode: '44',
        number: '1234567890',
      },
    },
    eligibilityDetailsAndAuthorisation: {
      isConnectedToExistingFacility: false,
      agreementType: 'ENERGY_INTENSIVE',
    },
    facilityExtent: {
      manufacturingProcessFile: '47179440-0990-4a4e-9565-fc508c0af648',
      processFlowFile: '4060eeb3-87ab-4be1-93f3-c506c66cca71',
      annotatedSitePlansFile: '5fa23bf5-330f-4691-94dd-82429e2686a1',
      eligibleProcessFile: '108118e5-d9d7-49a7-a4b6-bc3be6f0e711',
      areActivitiesClaimed: false,
    },
    apply70Rule: {
      energyConsumed: '70',
      energyConsumedEligible: '100',
      evidenceFile: '32801f8a-060e-4a4f-90fc-b85175b9e637',
    },
    cca3BaselineAndTargets: {
      targetComposition: {
        calculatorFile: '099b0b27-3a3c-4f9e-af2e-661804f617b0',
        measurementType: 'ENERGY_KWH',
        agreementCompositionType: 'NOVEM',
      },
      baselineData: {
        isTwelveMonths: true,
        baselineDate: '2022-01-01',
        greenfieldEvidences: [],
        usedReportingMechanism: true,
        energyCarbonFactor: '545',
      },
      facilityBaselineEnergyConsumption: {
        totalFixedEnergy: '150',
        hasVariableEnergy: true,
        variableEnergyType: 'BY_PRODUCT',
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'prod1',
            baselineYear: 2024,
            productStatus: 'LIVE',
            energy: '234',
            throughput: '65',
            throughputUnit: 'unit',
          },
          {
            productName: 'prod2',
            baselineYear: 2022,
            productStatus: 'LIVE',
            energy: '234',
            throughput: '68',
            throughputUnit: 'unit',
          },
        ],
      },
      facilityTargets: {
        improvements: {
          TP7: '21',
          TP8: '30',
          TP9: '40',
        },
      },
    },
  },
];

export const mockTP6Details: TargetPeriod6Details = {
  targetComposition: {
    calculatorFile: '1b2ae8fe-4322-4b8c-9b2c-03d77b5a8fbb',
    measurementType: 'ENERGY_KWH',
    agreementCompositionType: 'RELATIVE',
    isTargetUnitThroughputMeasured: true,
    throughputUnit: 'GJ',
    conversionFactor: '1',
    conversionEvidences: ['1b2af8ce-4311-4a8c-9a2c-03d77b5a8fbb'],
  },
  baselineData: {
    isTwelveMonths: false,
    baselineDate: '2020-12-12T00:00:00.000Z',
    explanation: 'test',
    greenfieldEvidences: ['f8dff40f-3fb7-4368-8723-244661fb686f'],
    energy: '100',
    usedReportingMechanism: true,
    throughput: '10',
    energyCarbonFactor: '1',
    performance: '10',
  },
  targets: {
    improvement: '0.1',
    target: '9',
  },
};

export const mockUnAVariationDetails: UnderlyingAgreementVariationDetails = {
  reason: 'variation details readon',
  modifications: [
    'AMEND_OPERATOR_OR_ORGANISATION_NAME',
    'AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS',
    'AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS',
    'REVIEW_OF_70_PERCENT_RULE',
    'ANY_CHANGES_NOT_COVERED',
  ],
};

export const mockUnAVariation: UnderlyingAgreementVariationPayload = {
  underlyingAgreementTargetUnitDetails: {
    operatorName: 'tu3-oper1',
    operatorAddress: {
      line1: 'addr1',
      line2: 'asd',
      city: 'kjhn',
      postcode: '87678',
      county: '765476',
      country: 'GB-SCT',
    },
    responsiblePersonDetails: {
      firstName: 'resp1',
      lastName: 'user',
      email: 'resp1@cca.uk',
      address: {
        line1: 'addr1',
        line2: 'asd',
        city: 'kjhn',
        postcode: '87678',
        county: '765476',
        country: 'GB-SCT',
      },
    },
    operatorType: 'LIMITED_COMPANY',
    isCompanyRegistrationNumber: false,
    registrationNumberMissingReason: 'no registration number',
  },
  facilities,
  targetPeriod5Details: {
    exist: false,
  },
  targetPeriod6Details: mockTP6Details,
  authorisationAndAdditionalEvidence: {
    authorisationAttachmentIds: ['fc975251-cf2e-415f-954c-8c4714b59a86'],
    additionalEvidenceAttachmentIds: ['1d203041-f766-4e7c-b378-12065f1db8c4'],
  },
  underlyingAgreementVariationDetails: mockUnAVariationDetails,
};

export const mockTargetUnitDetails: TargetUnitAccountDetails = {
  operatorName: 'tu3-oper1',
  operatorType: 'LIMITED_COMPANY',
  registrationNumberMissingReason: 'no registration number',
  address: {
    line1: 'addr1',
    line2: 'asd',
    city: 'kjhn',
    postcode: '87678',
    county: '765476',
    country: 'GB-SCT',
  },
  responsiblePerson: {
    email: 'resp1@cca.uk',
    firstName: 'resp1',
    lastName: 'user',
    jobTitle: 'job',
    address: {
      line1: 'addr1',
      line2: 'asd',
      city: 'kjhn',
      postcode: '87678',
      county: '765476',
      country: 'GB-SCT',
    },
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
  administrativeContactDetails: {
    email: 'administr1@cca.uk',
    firstName: 'administr1',
    lastName: 'user',
    jobTitle: 'job',
    address: {
      line1: 'addr1',
      city: 'kjhn',
      postcode: '87678',
      country: 'GB-NIR',
    },
    phoneNumber: {
      countryCode: '44',
      number: '1234567890',
    },
  },
  sectorAssociationId: 2,
};

export const mockOriginalUnAContainer: UnderlyingAgreementContainer = {
  schemeDataMap: {
    CCA_2: {
      sectorMeasurementType: 'ENERGY_KWH',
    },
    CCA_3: {
      sectorMeasurementType: 'ENERGY_KWH',
    },
  },
  underlyingAgreement: {
    facilities: facilitiesOriginal,
    targetPeriod5Details: {
      exist: false,
    },
    targetPeriod6Details: mockTP6Details,
    authorisationAndAdditionalEvidence: {
      authorisationAttachmentIds: ['fc975251-cf2e-415f-954c-8c4714b59a86'],
      additionalEvidenceAttachmentIds: [],
    },
  },
  underlyingAgreementAttachments: {
    '099b0b27-3a3c-4f9e-af2e-661804f617b0': 'METS Project Retro.xlsx',
    '108118e5-d9d7-49a7-a4b6-bc3be6f0e711': 'sample_profile1.png',
    '32801f8a-060e-4a4f-90fc-b85175b9e637': 'METS Project Retro.xlsx',
    '4060eeb3-87ab-4be1-93f3-c506c66cca71': 'sample_profile1.png',
    '47179440-0990-4a4e-9565-fc508c0af648': 'sample_profile1.png',
    '5fa23bf5-330f-4691-94dd-82429e2686a1': 'sample_profile1.png',
    'fc975251-cf2e-415f-954c-8c4714b59a86': 'sample_profile1.png',
  },
};

export const mockUnAVariationRegulatorLedRequestTaskPayload: UNAVariationRegulatorLedRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD',
  sendEmailNotification: true,
  workflowSchemeVersion: 'CCA_3',
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      schemeDataMap: {
        CCA_2: {
          sectorMeasurementType: 'ENERGY_KWH',
        },
        CCA_3: {
          sectorMeasurementType: 'ENERGY_KWH',
        },
      },
    },
  },
  originalUnderlyingAgreementContainer: mockOriginalUnAContainer,
  underlyingAgreement: mockUnAVariation,
  sectionsCompleted: {
    'ADS_2-F00003': 'UNCHANGED',
    targetPeriod5Details: 'UNCHANGED',
    targetPeriod6Details: 'UNCHANGED',
    authorisationAndAdditionalEvidence: 'IN_PROGRESS',
    underlyingAgreementTargetUnitDetails: 'UNCHANGED',
  },
  underlyingAgreementAttachments: {
    '099b0b27-3a3c-4f9e-af2e-661804f617b0': 'METS Project Retro.xlsx',
    '108118e5-d9d7-49a7-a4b6-bc3be6f0e711': 'sample_profile1.png',
    '32801f8a-060e-4a4f-90fc-b85175b9e637': 'METS Project Retro.xlsx',
    '4060eeb3-87ab-4be1-93f3-c506c66cca71': 'sample_profile1.png',
    '47179440-0990-4a4e-9565-fc508c0af648': 'sample_profile1.png',
    '5fa23bf5-330f-4691-94dd-82429e2686a1': 'sample_profile1.png',
    'fc975251-cf2e-415f-954c-8c4714b59a86': 'sample_profile1.png',
  },
  facilityChargeStartDateMap: {},
  regulatorLedSubmitAttachments: {
    '1d203041-f766-4e7c-b378-12065f1db8c4': 'sample_profile1.png',
    '7ff90de6-214b-479d-968b-f6721c7980a6': 'METS Project Retro.xlsx',
    '82aa7b42-adc6-4595-94d7-fbc947c55155': 'sample_profile1.png',
    'c7a50df9-5212-4131-9e21-0aea1188e039': 'sample_profile.bmp',
    'd9e49f22-ec69-4220-9424-e8a869d48377': 'sample_profile1.png',
    'da2f7fa7-4251-44ff-9f46-80ef4e288507': 'METS Project Retro.xlsx',
  },
};

export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 263,
    type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT',
    payload: mockUnAVariationRegulatorLedRequestTaskPayload,
    assignable: true,
    assigneeUserId: '38ddb238-97e7-4dd1-9799-57d5a43a6ce2',
    assigneeFullName: 'Regulator England',
    startDate: '2026-01-28T11:25:39.119249Z',
  },
};

export const mockRequestTaskState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTO,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
