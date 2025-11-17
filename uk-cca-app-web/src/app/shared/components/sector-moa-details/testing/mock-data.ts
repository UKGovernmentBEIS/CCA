import {
  SubsistenceFeesMoaDetailsDTO,
  SubsistenceFeesMoaFacilitySearchResultInfoDTO,
  SubsistenceFeesMoaFacilitySearchResults,
  SubsistenceFeesMoaSearchResultInfoDTO,
  SubsistenceFeesMoaTargetUnitDetailsDTO,
  SubsistenceFeesMoaTargetUnitSearchResultInfoDTO,
  SubsistenceFeesMoaTargetUnitSearchResults,
  SubsistenceFeesRunDetailsDTO,
} from 'cca-api';

export const mockFacilitiesList: SubsistenceFeesMoaFacilitySearchResultInfoDTO[] = [
  {
    moaFacilityId: 1,
    facilityBusinessId: 'ADS_52-F00001',
    facilityName: 'fac52-1',
    markFacilitiesStatus: 'IN_PROGRESS',
  },
  {
    moaFacilityId: 2,
    facilityBusinessId: 'ADS_52-F00002',
    facilityName: 'fac52-2',
    markFacilitiesStatus: 'IN_PROGRESS',
  },
  {
    moaFacilityId: 3,
    facilityBusinessId: 'ADS_52-F00003',
    facilityName: 'fac52-3',
    markFacilitiesStatus: 'COMPLETED',
  },
  {
    moaFacilityId: 4,
    facilityBusinessId: 'ADS_52-F00004',
    facilityName: 'fac52-4',
    markFacilitiesStatus: 'CANCELLED',
  },
];

export const mockTargetUnitFacilitiesListSearchResult: SubsistenceFeesMoaFacilitySearchResults = {
  subsistenceFeesMoaFacilities: mockFacilitiesList,
  total: mockFacilitiesList.length,
};

export const mockMoATargetUnitDetails: SubsistenceFeesMoaTargetUnitDetailsDTO = {
  moaTargetUnitId: 2,
  businessId: 'ADS_52-T00001',
  name: 'tu52-oper1',
  initialTotalAmount: '370',
  submissionDate: '2025-03-13',
  facilityFee: '185',
  currentTotalAmount: '370',
  totalFacilities: 2,
  paidFacilities: 0,
};

export const mockTargetUnitsList: SubsistenceFeesMoaTargetUnitSearchResultInfoDTO[] = [
  {
    moaTargetUnitId: 1,
    businessId: 'ADS_1-T00001',
    name: 'Flying Company 1',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '185',
  },
  {
    moaTargetUnitId: 2,
    businessId: 'ADS_1-T00002',
    name: 'Flying Company 2',
    markFacilitiesStatus: 'COMPLETED',
    currentTotalAmount: '185',
  },
  {
    moaTargetUnitId: 3,
    businessId: 'ADS_1-T00003',
    name: 'Flying Company 3',
    markFacilitiesStatus: 'CANCELLED',
    currentTotalAmount: '220',
  },
  {
    moaTargetUnitId: 4,
    businessId: 'ADS_52-T00004',
    name: 'tu52-oper4',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '370',
  },
];

export const mockTargetUnitsListSearchResult: SubsistenceFeesMoaTargetUnitSearchResults = {
  subsistenceFeesMoaTargetUnits: mockTargetUnitsList,
  total: mockTargetUnitsList.length,
};

export const mockSentSubsistenceFeesDetails: SubsistenceFeesRunDetailsDTO = {
  runId: 1,
  paymentRequestId: 'S2501',
  submissionDate: '2025-01-01',
  paymentStatus: 'AWAITING_PAYMENT',
  initialTotalAmount: '1000',
  currentTotalAmount: '900',
  outstandingTotalAmount: '599',
  sectorMoasCount: 2,
  targetUnitMoasCount: 1,
};

export const mockSectorMoas: SubsistenceFeesMoaSearchResultInfoDTO[] = [
  {
    moaId: 1,
    transactionId: 'CCACM1200',
    businessId: 'ADS_1',
    name: 'Aerospace_1',
    paymentStatus: 'AWAITING_PAYMENT',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '400',
    outstandingTotalAmount: '300',
    submissionDate: '2025-03-13',
  },
  {
    moaId: 2,
    transactionId: 'CCACM1201',
    businessId: 'ADS_2',
    name: 'Aerospace_2',
    paymentStatus: 'AWAITING_PAYMENT',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '400',
    outstandingTotalAmount: '200',
    submissionDate: '2025-03-14',
  },
];

export const mockTargetUnitMoas: SubsistenceFeesMoaSearchResultInfoDTO[] = [
  {
    moaId: 3,
    transactionId: 'CCATM1200',
    businessId: 'ADS_2-T00001',
    name: 'tu2-oper1',
    paymentStatus: 'AWAITING_PAYMENT',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '100',
    outstandingTotalAmount: '99',
  },
];

export const mockSectorMoaDetails: SubsistenceFeesMoaDetailsDTO = {
  moaId: 1,
  transactionId: 'CCACM01201',
  businessId: 'ADS_1',
  name: 'Aerospace_1',
  moaDocument: {
    name: '2025 Sector MoA - ADS_1 - CCACM01201.pdf',
    uuid: '877354c8-bc25-47b1-b4bb-c837317438ce',
  },
  submissionDate: '2025-02-27',
  paymentStatus: 'AWAITING_PAYMENT',
  totalFacilities: 1,
  paidFacilities: 0,
  initialTotalAmount: '185',
  currentTotalAmount: '185',
  receivedAmount: '0',
  facilityFee: '185',
};

export const mockTuMoaDetails: SubsistenceFeesMoaDetailsDTO = {
  moaId: 4,
  transactionId: 'CCATM01200',
  businessId: 'ADS_53-T00001',
  name: 'tu53-oper1',
  moaDocument: {
    name: '2025 Target Unit MoA - ADS_53-T00001 - CCATM01200.pdf',
    uuid: '7ea4423a-d131-400a-beaf-019a2941b72e',
  },
  submissionDate: '2025-02-27',
  paymentStatus: 'AWAITING_PAYMENT',
  totalFacilities: 2,
  paidFacilities: 0,
  initialTotalAmount: '370',
  currentTotalAmount: '370',
  receivedAmount: '0',
  facilityFee: '185',
};
