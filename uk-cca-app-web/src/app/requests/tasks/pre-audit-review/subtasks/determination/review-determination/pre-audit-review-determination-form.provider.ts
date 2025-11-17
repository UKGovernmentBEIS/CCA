import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { AuditDetermination } from 'cca-api';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';

export type PreAuditReviewDeterminationFormModel = FormGroup<{
  reviewCompletionDate: FormControl<Date>;
  furtherAuditNeeded: FormControl<AuditDetermination['furtherAuditNeeded']>;
  reviewComments: FormControl<AuditDetermination['reviewComments']>;
}>;

export const PRE_AUDIT_REVIEW_DETERMINATION_FORM = new InjectionToken<PreAuditReviewDeterminationFormModel>(
  'Pre-audit review determination form',
);

export const PreAuditReviewAuditReasonFormProvider: Provider = {
  provide: PRE_AUDIT_REVIEW_DETERMINATION_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const auditDetermination = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.auditDetermination;

    return fb.group({
      reviewCompletionDate: fb.control(
        auditDetermination?.reviewCompletionDate ? new Date(auditDetermination?.reviewCompletionDate) : null,
        { validators: [GovukValidators.required('The date must be today or in the past')] },
      ),
      furtherAuditNeeded: fb.control(auditDetermination?.furtherAuditNeeded ?? null, {
        validators: [GovukValidators.required('Make a selection')],
      }),
      reviewComments: fb.control(auditDetermination?.reviewComments ?? null, {}),
    });
  },
};
