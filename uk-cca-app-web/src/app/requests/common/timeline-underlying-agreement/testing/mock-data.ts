import {
  RequestActionDTO,
  UnderlyingAgreementPayload,
  UnderlyingAgreementSubmittedRequestActionPayload,
  UnderlyingAgreementVariationPayload,
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
    companyRegistrationNumber: '1111',
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
      conversionFactor: 1,
      conversionEvidences: ['conversionEvidence'],
    },
    baselineData: {
      isTwelveMonths: false,
      baselineDate: '2020-12-12T00:00:00.000Z',
      explanation: 'test',
      greenfieldEvidences: ['greenfieldEvidence'],
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
    },
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
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
