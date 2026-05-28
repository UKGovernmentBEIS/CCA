import { RequestTaskState } from '@netz/common/store';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

export const mockTprPayload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload = {
  payloadType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD',
  referenceData: {
    baselineAndTargets: {
      measurementType: 'ENERGY_KWH',
      usedReportingMechanism: true,
      improvements: { TP7: '8', TP8: '12', TP9: '15' },
    },
  },
  performanceData: {
    energyFuelDetails: {
      standardFuels: {
        NATURAL_GAS: {
          deliveredEnergy: '1000',
          primaryEnergy: '1000',
        },
        GRID_ELECTRICITY: {
          deliveredEnergy: '500',
          primaryEnergy: '1050',
        },
      },
      nonStandardFuels: [],
      atLeastSeventyPercentEnergyUsed: true,
      electricitySuppliedFromCHP: '200',
    },
    throughputDetails: {
      actualThroughput: '0',
      targetImprovement: '0',
      adjustedThroughput: '0',
      totalTargetVariableEnergy: '0',
      variableEnergyConsumptionDataByProduct: [],
    },
    calculatedResults: {
      actualEnergyCarbon: '0',
      targetEnergyCarbon: '0',
      energyCarbonDifference: '0',
      targetImprovement: '0',
      weightedConversionFactor: '0',
      targetCo2Emissions: '0',
      actualCo2Emissions: '0',
      co2EmissionsDifference: '0',
      actualImprovement: '0',
    },
  },
  sectionsCompleted: {},
};

export const mockTprPayloadNoFuels: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload = {
  ...mockTprPayload,
  performanceData: {
    energyFuelDetails: {
      standardFuels: {},
      nonStandardFuels: [],
      atLeastSeventyPercentEnergyUsed: true,
    },
    throughputDetails: {
      actualThroughput: '0',
      targetImprovement: '0',
      adjustedThroughput: '0',
      totalTargetVariableEnergy: '0',
      variableEnergyConsumptionDataByProduct: [],
    },
    calculatedResults: {
      actualEnergyCarbon: '0',
      targetEnergyCarbon: '0',
      energyCarbonDifference: '0',
      targetImprovement: '0',
      weightedConversionFactor: '0',
      targetCo2Emissions: '0',
      actualCo2Emissions: '0',
      co2EmissionsDifference: '0',
      actualImprovement: '0',
    },
  },
};

const mockTprRequestTaskItem = {
  requestTask: {
    id: 42,
    type: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT',
    payload: mockTprPayload,
    assignable: true,
    assigneeUserId: 'user-1',
    assigneeFullName: 'Test User',
    startDate: '2024-01-01T00:00:00Z',
  },
  allowedRequestTaskActions: ['PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION'],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_1-TPR001',
    type: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM',
    competentAuthority: 'ENGLAND',
    accountId: 1,
    requestMetadata: { type: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM' },
  },
};

export const mockTprRequestTaskState: RequestTaskState = {
  requestTaskItem: mockTprRequestTaskItem,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: true,
};

export const mockTprRequestTaskStateNoFuels: RequestTaskState = {
  ...mockTprRequestTaskState,
  requestTaskItem: {
    ...mockTprRequestTaskItem,
    requestTask: {
      ...mockTprRequestTaskItem.requestTask,
      payload: mockTprPayloadNoFuels,
    },
  },
};

export const mockTprRequestTaskStateReadOnly: RequestTaskState = {
  ...mockTprRequestTaskState,
  isEditable: false,
};

export const mockTprRequestTaskStateChpZero: RequestTaskState = {
  ...mockTprRequestTaskState,
  requestTaskItem: {
    ...mockTprRequestTaskItem,
    requestTask: {
      ...mockTprRequestTaskItem.requestTask,
      payload: {
        ...mockTprPayload,
        performanceData: {
          energyFuelDetails: {
            standardFuels: {},
            nonStandardFuels: [],
            atLeastSeventyPercentEnergyUsed: true,
            electricitySuppliedFromCHP: '0',
          },
          throughputDetails: {
            actualThroughput: '0',
            targetImprovement: '0',
            adjustedThroughput: '0',
            totalTargetVariableEnergy: '0',
            variableEnergyConsumptionDataByProduct: [],
          },
          calculatedResults: {
            actualEnergyCarbon: '0',
            targetEnergyCarbon: '0',
            energyCarbonDifference: '0',
            targetImprovement: '0',
            weightedConversionFactor: '0',
            targetCo2Emissions: '0',
            actualCo2Emissions: '0',
            co2EmissionsDifference: '0',
            actualImprovement: '0',
          },
        },
      } as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
    },
  },
};

export const mockTprPayloadThroughputTotalsOnly: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload = {
  ...mockTprPayload,
  targetPeriodType: 'TP8',
  reportType: 'FINAL',
  targetPeriodYear: 2027,
  referenceData: {
    baselineAndTargets: {
      measurementType: 'ENERGY_KWH',
      usedReportingMechanism: false,
      variableEnergyType: 'TOTALS',
      baselineVariableEnergy: '5000',
      totalThroughput: '10000',
      throughputUnit: 'tonnes',
      improvements: { TP5: '5', TP6: '6', TP7: '8', TP8: '12', TP9: '15' },
    },
  },
  performanceData: {
    energyFuelDetails: {
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '100', primaryEnergy: '210' },
        NON_GRID_ELECTRICITY: { deliveredEnergy: '50', primaryEnergy: '50' },
      },
      nonStandardFuels: [],
      atLeastSeventyPercentEnergyUsed: true,
      electricitySuppliedFromCHP: '25',
    },
    throughputDetails: {
      actualThroughput: '8000',
      targetImprovement: '12',
      adjustedThroughput: '8000',
      totalTargetVariableEnergy: '3520',
      variableEnergyConsumptionDataByProduct: [],
    },
    calculatedResults: {
      actualEnergyCarbon: '0',
      targetEnergyCarbon: '0',
      energyCarbonDifference: '0',
      targetImprovement: '12',
      weightedConversionFactor: '0',
      targetCo2Emissions: '0',
      actualCo2Emissions: '0',
      co2EmissionsDifference: '0',
      actualImprovement: '0',
    },
  },
  sectionsCompleted: {},
};

export const mockTprRequestTaskStateThroughputTotalsOnly: RequestTaskState = {
  ...mockTprRequestTaskState,
  requestTaskItem: {
    ...mockTprRequestTaskState.requestTaskItem,
    requestTask: {
      ...mockTprRequestTaskState.requestTaskItem.requestTask,
      payload: mockTprPayloadThroughputTotalsOnly,
    },
  },
};
