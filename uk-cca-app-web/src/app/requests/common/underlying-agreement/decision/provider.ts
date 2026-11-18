import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { RequestTaskFileService } from '@shared/services';

import {
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { underlyingAgreementQuery, underlyingAgreementReviewQuery } from '../+state';
import { UPLOAD_DECISION_ATTACHMENT_TYPE } from '../types';
import { DecisionFormModel } from './type';

export const DECISION_FORM_PROVIDER = new InjectionToken<DecisionFormModel>('DECISION_FORM_PROVIDER');

export function decisionFormProvider(
  group:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
): Provider {
  return {
    provide: DECISION_FORM_PROVIDER,
    deps: [FormBuilder, RequestTaskStore, RequestTaskFileService],
    useFactory: (
      fb: FormBuilder,
      requestTaskStore: RequestTaskStore,
      requestTaskFileService: RequestTaskFileService,
    ) => {
      const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();
      const attachments = requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
      const decision = requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision(group))();

      const filesControl = requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        decision?.details?.files || [],
        attachments,
        UPLOAD_DECISION_ATTACHMENT_TYPE[requestTaskType],
      );

      return fb.group({
        type: fb.control(decision?.type ?? null, [
          GovukValidators.required('Select the option that corresponds to your decision on the information above.'),
        ]),
        notes: fb.control(decision?.details?.notes ?? null, [
          GovukValidators.maxLength(10000, 'The notes should not be more than 10000 characters'),
        ]),
        files: filesControl,
      });
    },
  };
}

export function facilityDecisionFormProvider(): Provider {
  return {
    provide: DECISION_FORM_PROVIDER,
    deps: [FormBuilder, RequestTaskStore, RequestTaskFileService, ActivatedRoute],
    useFactory: (
      fb: FormBuilder,
      requestTaskStore: RequestTaskStore,
      requestTaskFileService: RequestTaskFileService,
      route: ActivatedRoute,
    ) => {
      const requestTaskType = requestTaskStore.select(requestTaskQuery.selectRequestTaskType)();
      const attachments = requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
      const facilityId = route.snapshot.params.facilityId;
      const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();

      const decision = requestTaskStore.select(
        underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(facilityId),
      )();

      const filesControl = requestTaskFileService.buildFormControl(
        requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        decision?.details?.files || [],
        attachments,
        UPLOAD_DECISION_ATTACHMENT_TYPE[requestTaskType],
      );

      return fb.group(
        {
          type: fb.control(decision?.type ?? null, [
            GovukValidators.required('Select the option that corresponds to your decision on the information above.'),
          ]),
          notes: fb.control(decision?.details?.notes ?? null, [
            GovukValidators.maxLength(10000, 'The notes should not be more than 10000 characters'),
          ]),
          files: filesControl,
          changeDate: fb.control({
            value: [decision?.changeStartDate],
            disabled: facility.status !== 'NEW',
          }),
          startDate: fb.control({
            value: decision?.startDate ? new Date(decision?.startDate) : null,
            disabled: facility.status !== 'NEW',
          }),
        },
        { validators: [startDateValidator] },
      );
    },
  };
}

const startDateValidator: ValidatorFn = (form: FormGroup) => {
  if (!form) return {};

  const type = form.controls.type.value;
  const changeDate = form.controls.changeDate.value[0];
  const startDate = form.controls.startDate.value;

  if (type === 'REJECTED') return;
  if (changeDate && !startDate) form.controls.startDate.setErrors({ required: 'Select a date' });
  return null;
};
