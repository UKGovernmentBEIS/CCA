import {
  RequestActionDTO,
  UnderlyingAgreementPayload,
  UnderlyingAgreementSubmittedRequestActionPayload,
  UnderlyingAgreementVariationPayload,
  UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload,
  UnderlyingAgreementVariationSubmittedRequestActionPayload,
} from 'cca-api';

const mockUnderlyingAgreement: UnderlyingAgreementPayload = {
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
  facilities: [
    {
      facilityId: 'ADS_1-F00001',
      status: 'NEW',
      facilityDetails: {
        name: 'Facility 1',
        participatingSchemeVersions: ['CCA_2'],
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
        energyConsumed: '50',
        energyConsumedProvision: '40',
        energyConsumedEligible: '70',
        evidenceFile: 'evidenceFile',
      },
    },
  ],
  targetPeriod5Details: {
    exist: false,
  },
  targetPeriod6Details: {
    targetComposition: {
      calculatorFile: 'calculatorFile',
      measurementType: 'ENERGY_KWH',
      agreementCompositionType: 'RELATIVE',
      isTargetUnitThroughputMeasured: true,
      throughputUnit: 'GJ',
      conversionFactor: '1',
      conversionEvidences: ['conversionEvidence'],
    },
    baselineData: {
      isTwelveMonths: false,
      baselineDate: '2020-12-12T00:00:00.000Z',
      explanation: 'test',
      greenfieldEvidences: ['greenfieldEvidence'],
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
  },
  authorisationAndAdditionalEvidence: {
    authorisationAttachmentIds: ['authorisationAttachment'],
    additionalEvidenceAttachmentIds: [],
  },
};

const mockUnderlyingAgreementVariation: UnderlyingAgreementVariationPayload = {
  ...mockUnderlyingAgreement,
  underlyingAgreementVariationDetails: {
    reason: 'No reason',
    modifications: ['AMEND_OPERATOR_OR_ORGANISATION_NAME', 'STRUCTURAL_CHANGE'],
  },
};

const mockPayload: UnderlyingAgreementSubmittedRequestActionPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: {
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
    },
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      schemeDataMap: {
        ['CCA_2']: { sectorMeasurementType: 'ENERGY_KWH' },
      },
    },
  },
  underlyingAgreement: mockUnderlyingAgreement,
  underlyingAgreementAttachments: {
    manufacturingProcessFile: 'manufacturingProcessFile.xlsx',
    processFlowFile: 'processFlowFile.xlsx',
    annotatedSitePlansFile: 'annotatedSitePlansFile.xlsx',
    eligibleProcessFile: 'eligibleProcessFile.xlsx',
    activitiesDescriptionFile: 'activitiesDescriptionFile.xlsx',
    evidenceFile: 'evidenceFile.xlsx',
    authorisationAttachment: 'authorisationAttachment.xlsx',
    calculatorFile: 'calculatorFile.xls',
    conversionEvidence: 'conversionEvidence.xlsx',
    greenfieldEvidence: 'greenfieldEvidence.xlsx',
  },
};

const mockVariationPayload: UnderlyingAgreementVariationSubmittedRequestActionPayload = {
  ...mockPayload,
  underlyingAgreement: mockUnderlyingAgreementVariation,
};

const mockUnARegulatorLedVariationPayload: UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMITTED_PAYLOAD',
  accountReferenceData: {
    targetUnitAccountDetails: {
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
    },
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
  underlyingAgreement: {
    underlyingAgreementVariationDetails: {
      reason: 'asdasdsa',
      modifications: ['AMEND_OPERATOR_OR_ORGANISATION_NAME'],
    },
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
    facilities: [
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
    ],
    authorisationAndAdditionalEvidence: {
      authorisationAttachmentIds: ['fc975251-cf2e-415f-954c-8c4714b59a86'],
      additionalEvidenceAttachmentIds: [],
    },
  },
  sectionsCompleted: {
    'ADS_2-F00003': 'UNCHANGED',
    targetPeriod5Details: 'UNCHANGED',
    targetPeriod6Details: 'UNCHANGED',
    operatorAssentDecision: 'COMPLETED',
    authorisationAndAdditionalEvidence: 'UNCHANGED',
    underlyingAgreementVariationDetails: 'COMPLETED',
    underlyingAgreementTargetUnitDetails: 'COMPLETED',
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
  determination: {
    variationImpactsAgreement: true,
    additionalInformation: 'sakjhd kjash d',
    files: ['6773042a-dac0-44c5-af3e-b58159a96d77'],
  },
  decisionNotification: {
    signatory: '38ddb238-97e7-4dd1-9799-57d5a43a6ce2',
  },
  defaultContacts: [
    {
      name: 'resp1 user',
      email: 'resp1@cca.uk',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'administr1 user',
      email: 'administr1@cca.uk',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
    {
      name: 'Fred_2 William_2',
      email: 'fredwilliam_2@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
  usersInfo: {
    '38ddb238-97e7-4dd1-9799-57d5a43a6ce2': {
      name: 'Regulator England',
    },
  },
  underlyingAgreementDocuments: {
    CCA_3: {
      name: 'ADS_2-T00003 CCA3 Underlying Agreement v2 [proposed].pdf',
      uuid: 'e1058482-f97d-4f4b-b067-ae367b08b175',
    },
  },
  officialNotices: [
    {
      name: 'Proposed underlying agreement variation cover letter.pdf',
      uuid: 'a32b8507-cb59-4cd8-bcfc-6284511971a9',
    },
  ],
  regulatorLedSubmitAttachments: {
    '6773042a-dac0-44c5-af3e-b58159a96d77': 'sample_profile1.png',
  },
};

export const mockUnderlyingAgreementSubmittedRequestAction: RequestActionDTO = {
  id: 14,
  type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED',
  payload: mockPayload,
  requestId: 'ADS_1-T00004-UNA',
  requestType: 'UNDERLYING_AGREEMENT',
  requestAccountId: 4,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-08-30T11:44:57.536474Z',
};

export const mockUnderlyingAgreementVariationSubmittedRequestAction: RequestActionDTO = {
  id: 15,
  type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED',
  payload: mockVariationPayload,
  requestId: 'ADS_1-T00004-VAR-1',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 4,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2024-08-30T11:44:57.536474Z',
};

export const mockUnARegulatorLedVariationSubmittedRequestAction: RequestActionDTO = {
  id: 219,
  type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED',
  payload: mockUnARegulatorLedVariationPayload,
  requestId: 'ADS_2-T00003-VAR-7',
  requestType: 'UNDERLYING_AGREEMENT_VARIATION',
  requestAccountId: 22,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2026-02-16T10:52:12.980715Z',
};
