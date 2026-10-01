import { FacilityPATReportsState } from '../../facility-pat-report.store';
import { FacilityTargetPeriodReportsState } from '../../facility-target-period-report.store';

export const mockFacilityPATStore: FacilityPATReportsState = {
  reportInfo: {
    targetPeriodYear: 2026,
    reportVersion: 2,
    submissionDate: '2026-08-27T11:41:31.997642Z',
  },
  reportingYear: '2026',
  details: {
    submissionDate: '2026-06-05',
    targetPeriodYear: 2026,
    data: {
      savingActions: [
        {
          actionCategoryType: 'ENERGY_MANAGEMENT',
          supplyDemandSideMeasure: 'DEMAND_SIDE',
          savingActionsImplemented: 'Created an energy management system',
          reasonsForImplementation: 'It seemed like a good idea at the time',
          implementationDate: '2026-01-01',
          fixedEnergyConsumptionOrCarbonEmissionsImpacted: 'FIXED_AND_VARIABLE',
          energyConsumptionOrCarbonEmissionsImpactedPercentage: '10.0000000',
          expectedExtentOfChangeImplementedPercentage: '20.0000000',
          expectedSavingsFromTheChangeImplementedPercentage: '-586.0000000',
          estimatedChangeInEnergyConsumptionPercentage: '-11.7200000',
        },
      ],
    },
  },
};

export const mockFacilityTPRStore: FacilityTargetPeriodReportsState = {
  statusInfo: [
    {
      targetPeriodType: 'TP7',
      targetPeriodName: 'TP7 (2026)',
      targetPeriodYear: 2026,
      variationIndicator: false,
      locked: false,
      reportVersion: 4,
      submissionDate: '2026-06-05',
      lockEditable: false,
      variationIndicatorEditable: true,
      reportType: 'FINAL',
      submissionType: 'SECONDARY',
    },
  ],
  details: {
    targetPeriod: 'TP7',
    atLeastSeventyPercentEnergyUsed: true,
    calculatedResults: {
      actualEnergyCarbon: '3.1000000',
      targetEnergyCarbon: '109.4750995',
      energyCarbonDifference: '-106.3750995',
      targetImprovement: '0.2000000',
      weightedConversionFactor: '18.9037665',
      targetCo2Emissions: '2.0694917',
      actualCo2Emissions: '0.0586017',
      co2EmissionsDifference: '-2.0108900',
      actualImprovement: '0.9765861',
      targetPeriodResultType: 'TARGET_MET',
      surplusGained: '2',
      buyOutRequired: '0',
    },
    baselineAndTargets: {
      baselineDate: '2022-01-01',
      isTwelveMonths: true,
      energyCarbonFactor: '123',
      measurementType: 'ENERGY_GJ',
      usedReportingMechanism: true,
      improvements: {
        TP7: '20',
        TP8: '30',
        TP9: '40',
      },
      totalFixedEnergy: '100',
      variableEnergyType: 'BY_PRODUCT',
      variableEnergyConsumptionDataByProduct: [
        {
          productName: 'prod1',
          baselineYear: 2023,
          productStatus: 'LIVE',
          energy: '234',
          throughput: '65',
          throughputUnit: 'unit',
          energyCarbonIntensity: '3.6000000',
        },
        {
          productName: 'prod2',
          baselineYear: 2025,
          productStatus: 'LIVE',
          energy: '234',
          throughput: '65',
          throughputUnit: 'unit',
          energyCarbonIntensity: '3.6000000',
        },
      ],
    },
  },
  reportType: 'FINAL',
  targetPeriodType: 'TP7',
};
