import { produce } from 'immer';

import {
  EmissionsTradingSchemeDataInputEnergyFuelDetails,
  EmissionsTradingSchemeDataSaveRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsReportingTaskPayload } from './ets-reporting.types';
import { buildEtsFuelSummaryRows, createEtsFuelDetails } from './ets-reporting-calculations';

type EtsSaveDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: EmissionsTradingSchemeDataSaveRequestTaskActionPayload;
};

export function updateEtsUseAnswers(
  payload: EtsReportingTaskPayload,
  isUltraSmallEmitter: boolean,
  provideFuelDetails?: boolean,
): EtsReportingTaskPayload {
  return produce(payload, (draft) => {
    draft.emissionsTradingSchemeDataInputDetails = {
      ...draft.emissionsTradingSchemeDataInputDetails,
      isUltraSmallEmitter,
    };
    const input = draft.emissionsTradingSchemeDataInputDetails;
    if (isUltraSmallEmitter) {
      input.provideFuelDetails = provideFuelDetails;
      if (provideFuelDetails === false) delete input.energyFuelDetails;
    } else {
      delete input.provideFuelDetails;
    }
    draft.sectionsCompleted = {
      ...draft.sectionsCompleted,
      [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'IN_PROGRESS',
    };
  });
}

export function updateEtsFuelDetails(
  payload: EtsReportingTaskPayload,
  details: EmissionsTradingSchemeDataInputEnergyFuelDetails,
): EtsReportingTaskPayload {
  return produce(payload, (draft) => {
    if (!draft.emissionsTradingSchemeDataInputDetails) throw new Error('Answer the UK ETS USE questions first');
    draft.emissionsTradingSchemeDataInputDetails.energyFuelDetails = details;
    draft.sectionsCompleted = {
      ...draft.sectionsCompleted,
      [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'IN_PROGRESS',
    };
  });
}

export function removeEtsNonStandardFuel(payload: EtsReportingTaskPayload, index: number): EtsReportingTaskPayload {
  const details = payload.emissionsTradingSchemeDataInputDetails?.energyFuelDetails;
  if (!Number.isInteger(index) || !details?.nonStandardFuels?.[index]) {
    return payload;
  }

  const updatedDetails = produce(details, (draft) => {
    draft.nonStandardFuels.splice(index, 1);
  });
  const totals = createEtsFuelDetails(
    payload.measurementType,
    buildEtsFuelSummaryRows(payload.measurementType, updatedDetails),
  );
  return updateEtsFuelDetails(payload, {
    ...updatedDetails,
    totalPrimaryEnergy: totals.totalPrimaryEnergy,
    totalCo2Emissions: totals.totalCo2Emissions,
  });
}

export function createEtsSaveDto(
  requestTaskId: number,
  payload: EtsReportingTaskPayload,
  status: 'IN_PROGRESS' | 'COMPLETED' = 'IN_PROGRESS',
): EtsSaveDto {
  return {
    requestTaskId,
    requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD',
      emissionsTradingSchemeDataInputDetails: payload.emissionsTradingSchemeDataInputDetails,
      sectionsCompleted: { ...payload.sectionsCompleted, [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: status },
    },
  };
}

export function createEtsSubmitDto(requestTaskId: number): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION',
    requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
  };
}
