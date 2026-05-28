import {
  PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload,
  PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
  PerformanceDataFacilityInputData,
  RequestTaskActionProcessDTO,
} from 'cca-api';

type TprFormRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
};

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: PerformanceDataFacilityInputData,
  sectionsCompleted: Record<string, string>,
): TprFormRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_PAYLOAD',
      performanceData: payload,
      sectionsCompleted,
    },
  };
}

export function toPerformanceDataFacilityDigitalFormSavePayload(
  payload: PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
): PerformanceDataFacilityInputData {
  return {
    energyFuelDetails: payload?.performanceData?.energyFuelDetails,
    throughputDetails: payload?.performanceData?.throughputDetails,
    calculatedResults: payload?.performanceData?.calculatedResults,
  };
}
