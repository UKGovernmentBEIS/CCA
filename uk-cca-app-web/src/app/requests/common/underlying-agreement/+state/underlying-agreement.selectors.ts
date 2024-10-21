import { createDescendingSelector, requestTaskQuery, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  AccountReferenceData,
  AuthorisationAndAdditionalEvidence,
  Facility,
  SectorAssociationDetails,
  TargetUnitAccountDetails,
  UnderlyingAgreementPayload,
  UnderlyingAgreementTargetUnitDetails,
  UnderlyingAgreementVariationPayload,
} from 'cca-api';

import { TaskItemStatus } from '../../task-item-status';
import { ManageFacilities, UNARequestTaskPayload } from '../underlying-agreement.types';

const selectPayload: StateSelector<RequestTaskState, UNARequestTaskPayload> = createDescendingSelector(
  requestTaskQuery.selectRequestTaskPayload,
  (payload) => payload as UNARequestTaskPayload,
);

const selectAccountReferenceData: StateSelector<RequestTaskState, AccountReferenceData> = createDescendingSelector(
  selectPayload,
  (payload) => payload.accountReferenceData,
);

const selectAccountReferenceDataSectorAssociationDetails: StateSelector<RequestTaskState, SectorAssociationDetails> =
  createDescendingSelector(
    selectAccountReferenceData,
    (accountReferenceData) => accountReferenceData.sectorAssociationDetails,
  );

const selectAccountReferenceDataTargetUnitDetails: StateSelector<RequestTaskState, TargetUnitAccountDetails> =
  createDescendingSelector(
    selectAccountReferenceData,
    (accountReferenceData) => accountReferenceData.targetUnitAccountDetails,
  );

const selectSectionsCompleted: StateSelector<RequestTaskState, { [key: string]: string }> = createDescendingSelector(
  selectPayload,
  (payload) => payload.sectionsCompleted,
);

const selectUnderlyingAgreementSubmitAttachments: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(selectPayload, (payload) => payload?.underlyingAgreementAttachments);

const selectStatusForSubtask = (subtask: string): StateSelector<RequestTaskState, TaskItemStatus> => {
  return createDescendingSelector(
    selectSectionsCompleted,
    (completed) => (completed?.[subtask] as TaskItemStatus) ?? TaskItemStatus.NOT_STARTED,
  );
};

const selectAttachments: StateSelector<RequestTaskState, { [key: string]: string }> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreementAttachments,
);

const selectUnderlyingAgreement: StateSelector<
  RequestTaskState,
  UnderlyingAgreementPayload | UnderlyingAgreementVariationPayload
> = createDescendingSelector(selectPayload, (payload) => payload.underlyingAgreement);

const selectUnderlyingAgreementTargetUnitDetails: StateSelector<
  RequestTaskState,
  UnderlyingAgreementTargetUnitDetails
> = createDescendingSelector(
  selectPayload,
  (payload) => payload.underlyingAgreement.underlyingAgreementTargetUnitDetails,
);

const selectManageFacilities: StateSelector<RequestTaskState, ManageFacilities> = createDescendingSelector(
  selectUnderlyingAgreement,
  (underlyingAgreement) => ({
    facilityItems:
      underlyingAgreement?.facilities?.map((f) => ({
        name: f.facilityDetails.name,
        facilityId: f.facilityId,
        status: f.status,
      })) ?? [],
  }),
);

const selectFacility = (facilityId: string): StateSelector<RequestTaskState, Facility> => {
  return createDescendingSelector(selectUnderlyingAgreement, (underlyingAgreement) =>
    underlyingAgreement?.facilities ? underlyingAgreement.facilities.find((f) => f.facilityId === facilityId) : null,
  );
};

const selectCurrentFacilityId: StateSelector<RequestTaskState, string> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.currentFacilityId,
);

const selectAuthorisationAndAdditionalEvidence: StateSelector<RequestTaskState, AuthorisationAndAdditionalEvidence> =
  createDescendingSelector(
    selectUnderlyingAgreement,
    (underlyingAgreement) => underlyingAgreement.authorisationAndAdditionalEvidence,
  );

const selectTargetPeriodDetails = (isTP5: boolean) =>
  createDescendingSelector(selectUnderlyingAgreement, (una) =>
    isTP5 ? una.targetPeriod5Details.details : una.targetPeriod6Details,
  );

const selectTargetPeriodExists = createDescendingSelector(
  selectUnderlyingAgreement,
  (una) => una.targetPeriod5Details.exist,
);

const selectBaselineData = (isTP5: boolean) =>
  createDescendingSelector(selectUnderlyingAgreement, (una) =>
    isTP5 ? una.targetPeriod5Details.details.baselineData : una.targetPeriod6Details.baselineData,
  );

const selectTargets = (isTP5: boolean) =>
  createDescendingSelector(selectUnderlyingAgreement, (una) =>
    isTP5 ? una.targetPeriod5Details.details.targets : una.targetPeriod6Details.targets,
  );

const selectTargetComposition = (isTP5: boolean) =>
  createDescendingSelector(selectUnderlyingAgreement, (una) =>
    isTP5 ? una.targetPeriod5Details.details.targetComposition : una.targetPeriod6Details.targetComposition,
  );

export const underlyingAgreementQuery = {
  selectPayload,
  selectSectionsCompleted,
  selectUnderlyingAgreementSubmitAttachments,
  selectAccountReferenceData,
  selectAccountReferenceDataTargetUnitDetails,
  selectAccountReferenceDataSectorAssociationDetails,
  selectStatusForSubtask,
  selectAttachments,
  selectUnderlyingAgreement,
  selectUnderlyingAgreementTargetUnitDetails,
  selectManageFacilities,
  selectFacility,
  selectCurrentFacilityId,
  selectAuthorisationAndAdditionalEvidence,
  selectTargetPeriodDetails,
  selectTargetPeriodExists,
  selectBaselineData,
  selectTargets,
  selectTargetComposition,
};
