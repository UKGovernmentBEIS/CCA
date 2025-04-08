import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import {
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { underlyingAgreementReviewQuery } from './+state';

const selectDecision = (
  store: RequestTaskStore,
  route: ActivatedRouteSnapshot,
  decisionGroup?:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
) => {
  return decisionGroup
    ? store.select(underlyingAgreementReviewQuery.selectSubtaskDecision(decisionGroup))()
    : store.select(underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(route.paramMap.get('facilityId')))();
};

const selectReviewSectionIsCompleted = (store: RequestTaskStore, route: ActivatedRouteSnapshot, section: string) =>
  section === 'facilities'
    ? store.select(underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(route.paramMap.get('facilityId')))()
    : store.select(underlyingAgreementReviewQuery.selectReviewSectionIsCompleted(section))();

/**
 *
 * @param decisionGroup Decision group is null when you want the guard to search for a facilityId in the route
 */
export const canActivateReviewWizardStep: (
  decisionGroup:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | null,
  subtask: string,
  wizardStep: Record<string, string>,
) => CanActivateFn = (decisionGroup, subtask, wizardStep) => (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', wizardStep.SUMMARY]);

  const change = route.queryParamMap.get('change') === 'true';
  if (change) return true;

  const reviewSectionCompleted = selectReviewSectionIsCompleted(store, route, subtask);
  const decision = selectDecision(store, route, decisionGroup);

  if (!decision) return createUrlTreeFromSnapshot(route, ['../', wizardStep.DECISION]);

  if (!reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['../', wizardStep.CHECK_YOUR_ANSWERS]);

  return false;
};

/**
 *
 * @param decisionGroup Decision group is null when you want the guard to search for a facilityId in the route
 */
export const canActivateReviewCheckYourAnswers: (
  decisionGroup:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  wizardStep: Record<string, string>,
) => CanActivateFn = (decisionGroup, wizardStep) => (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', wizardStep.SUMMARY]);

  const decision = selectDecision(store, route, decisionGroup);
  if (!decision) return createUrlTreeFromSnapshot(route, ['../', wizardStep.DECISION]);

  return true;
};

/**
 *
 * @param decisionGroup Decision group is null when you want the guard to search for a facilityId in the route
 */
export const canActivateReviewSummary: (
  decisionGroup:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  subtask: string,
  wizardStep: Record<string, string>,
) => CanActivateFn = (decisionGroup, subtask, wizardStep) => (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return true;

  const reviewSectionCompleted = selectReviewSectionIsCompleted(store, route, subtask);
  if (reviewSectionCompleted) return true;

  const hasDecision = selectDecision(store, route, decisionGroup);
  if (!hasDecision) return createUrlTreeFromSnapshot(route, ['../', wizardStep.DECISION]);

  return createUrlTreeFromSnapshot(route, ['../', wizardStep.CHECK_YOUR_ANSWERS]);
};

/**
 *
 * @param decisionGroup Decision group is null when you want the guard to search for a facilityId in the route
 */
export const canActivateReviewDecision: (
  decisionGroup:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  subtask: string,
  wizardStep: Record<string, string>,
) => CanActivateFn = (decisionGroup, subtask, wizardStep) => (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', wizardStep.SUMMARY]);

  const change = route.queryParamMap.get('change') === 'true';
  if (change) return true;

  const reviewSectionCompleted = selectReviewSectionIsCompleted(store, route, subtask);
  if (reviewSectionCompleted) return createUrlTreeFromSnapshot(route, ['../', wizardStep.SUMMARY]);

  const hasDecision = selectDecision(store, route, decisionGroup);
  if (hasDecision) return createUrlTreeFromSnapshot(route, ['../', wizardStep.CHECK_YOUR_ANSWERS]);

  return true;
};
