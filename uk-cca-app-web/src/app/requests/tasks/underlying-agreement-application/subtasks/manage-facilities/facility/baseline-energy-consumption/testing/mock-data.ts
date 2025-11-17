import { FacilityBaselineEnergyConsumption, ProductVariableEnergyConsumptionData } from 'cca-api';

export const mockFacilityId = 'ADS_1-F00001';
export const mockRequestTaskId = 123;

export const mockProductVariableEnergyData: ProductVariableEnergyConsumptionData = {
  productName: 'Product 1',
  productStatus: 'NEW',
  baselineYear: 2022,
  energy: '100',
  throughput: '50',
  throughputUnit: 'tonnes',
};

export const mockBaselineEnergyConsumption: FacilityBaselineEnergyConsumption = {
  totalFixedEnergy: '100',
  hasVariableEnergy: true,
  variableEnergyType: 'TOTALS',
  baselineVariableEnergy: '200',
  totalThroughput: '50',
  throughputUnit: 'tonnes',
  variableEnergyConsumptionDataByProduct: [],
};

export const mockBaselineEnergyConsumptionWithProducts: FacilityBaselineEnergyConsumption = {
  ...mockBaselineEnergyConsumption,
  variableEnergyType: 'BY_PRODUCT',
  variableEnergyConsumptionDataByProduct: [mockProductVariableEnergyData],
};

export const mockBaselineEnergyConsumptionNoVariable: FacilityBaselineEnergyConsumption = {
  totalFixedEnergy: '100',
  hasVariableEnergy: false,
  totalThroughput: '50',
  throughputUnit: 'tonnes',
};

export const mockFacilityDetails = {
  name: 'Facility 1',
  isCoveredByUkets: false,
  applicationReason: 'NEW_AGREEMENT',
  facilityAddress: {
    line1: 'Facility Line1',
    line2: 'Facility Line2',
    city: 'Facility City',
    postcode: 'Facility 14',
    country: 'GR',
  },
};

export const mockFacility = {
  facilityId: mockFacilityId,
  facilityDetails: mockFacilityDetails,
  cca3BaselineAndTargets: {
    baselineData: {
      baselineDate: '2022-01-01',
    },
    targetComposition: {
      measurementType: 'ENERGY_KWH',
    },
    facilityBaselineEnergyConsumption: mockBaselineEnergyConsumption,
  },
};

export const mockFacilityWithProducts = {
  ...mockFacility,
  cca3BaselineAndTargets: {
    ...mockFacility.cca3BaselineAndTargets,
    facilityBaselineEnergyConsumption: mockBaselineEnergyConsumptionWithProducts,
  },
};

export const mockUnderlyingAgreement = {
  facilities: [mockFacility],
};

export const mockUnderlyingAgreementWithProducts = {
  facilities: [mockFacilityWithProducts],
};

export const mockRequestTaskPayload = {
  underlyingAgreement: mockUnderlyingAgreement,
};

export const mockRequestTaskPayloadWithProducts = {
  underlyingAgreement: mockUnderlyingAgreementWithProducts,
};

export const mockActivatedRoute = {
  snapshot: {
    params: { facilityId: mockFacilityId },
    pathFromRoot: [],
  },
};

export const mockActivatedRouteWithProductParams = {
  snapshot: {
    params: { facilityId: mockFacilityId },
    paramMap: {
      get: jest.fn((key: string) => {
        if (key === 'productIndex') return '0';
        if (key === 'productName') return 'Product 1';
        return null;
      }),
    },
    pathFromRoot: [],
  },
};

export const mockBaselineYearOptions = [
  { value: 2022, text: '2022' },
  { value: 2023, text: '2023' },
];
