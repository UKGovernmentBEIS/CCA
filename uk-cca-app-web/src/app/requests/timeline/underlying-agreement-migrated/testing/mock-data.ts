import { RequestActionState } from '@netz/common/store';

import {
  Facility,
  FacilityDetails,
  RequestActionDTO,
  UnderlyingAgreementMigratedRequestActionPayload,
  UnderlyingAgreementPayload,
  UnderlyingAgreementTargetUnitDetails,
} from 'cca-api';

const operatorAddress = {
  line1: 'test',
  city: 'test',
  postcode: 'test',
  county: 'test',
  country: 'GR',
};

const responsiblePersonDetails = {
  firstName: 'test',
  lastName: 'test',
  email: 'test@test.com',
  address: operatorAddress,
};

const underlyingAgreementTargetUnitDetails: UnderlyingAgreementTargetUnitDetails = {
  operatorName: 'Target Unit 22',
  operatorAddress: operatorAddress,
  responsiblePersonDetails: responsiblePersonDetails,
  operatorType: 'LIMITED_COMPANY',
  isCompanyRegistrationNumber: true,
  companyRegistrationNumber: 'test1234',
};

const facilityDetails: FacilityDetails = {
  name: 'aaa',
  isCoveredByUkets: false,
  applicationReason: 'NEW_AGREEMENT',
  facilityAddress: operatorAddress,
};

const facilityContact = {
  email: 'test@test.com',
  firstName: 'test',
  lastName: 'test',
  address: operatorAddress,
  phoneNumber: {
    countryCode: '30',
    number: '6999999999',
  },
};

const facilities: Array<Facility> = [
  {
    status: 'NEW',
    facilityId: 'ADS_2-F00029',
    facilityDetails: facilityDetails,
    facilityContact: facilityContact,
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

const underlyingAgreement: UnderlyingAgreementPayload = {
  underlyingAgreementTargetUnitDetails: underlyingAgreementTargetUnitDetails,
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

const officialNotice = {
  name: 'Underlying agreement rejection notice.pdf',
  uuid: 'ad2d77a4-9882-4bcd-bfc1-5f7818a527aa',
};

const migratedPayload: UnderlyingAgreementMigratedRequestActionPayload = {
  underlyingAgreement,
  underlyingAgreementAttachments,
  underlyingAgreementDocument: officialNotice,
  activationDate: '2024-10-10',
};

const mockMigratedRequestActionDTO: RequestActionDTO = {
  id: 64,
  type: 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED',
  payload: migratedPayload,
  requestId: 'ADS_2-T00026-UNA',
  requestType: 'UNDERLYING_AGREEMENT',
  requestAccountId: 45,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator 2',
  creationDate: '2024-09-25T18:21:32.751217Z',
};

export const mockRequestActionMigratedState: RequestActionState = {
  action: mockMigratedRequestActionDTO,
};
