import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { AdditionalNoticeRecipientDTO } from 'cca-api';

export type NotifyOperatorOfDecisionFormModel = FormGroup<{
  additionalUsersNotified?: FormControl<AdditionalNoticeRecipientDTO[]>;
  externalContactsNotified?: FormControl<number[]>;
  signatory: FormControl<string>;
}>;

export const NOTIFY_OPERATOR_OF_DECISION_FORM = new InjectionToken<NotifyOperatorOfDecisionFormModel>(
  'Notify operator of decision form',
);

export const NotifyOperatorOfDecisionFormProvider: Provider = {
  provide: NOTIFY_OPERATOR_OF_DECISION_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const assigneeRegulatorUserId = requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();

    return fb.group({
      additionalUsersNotified: fb.control<AdditionalNoticeRecipientDTO[]>([]),
      externalContactsNotified: fb.control<number[]>([]),
      signatory: fb.control<string>(assigneeRegulatorUserId ?? null, GovukValidators.required('Make a selection')),
    });
  },
};
