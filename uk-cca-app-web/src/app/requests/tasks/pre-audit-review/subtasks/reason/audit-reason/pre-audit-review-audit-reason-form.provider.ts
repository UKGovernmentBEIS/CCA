import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { AuditReasonDetails } from 'cca-api';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';

export type PreAuditReviewAuditReasonFormModel = {
  reasonsForAudit: FormControl<AuditReasonDetails['reasonsForAudit']>;
  comment: FormControl<AuditReasonDetails['comment']>;
};

export const PRE_AUDIT_REVIEW_AUDIT_REASON_FORM = new InjectionToken<PreAuditReviewAuditReasonFormModel>(
  'Pre-audit review audit reason form',
);

export const PreAuditReviewAuditReasonFormProvider: Provider = {
  provide: PRE_AUDIT_REVIEW_AUDIT_REASON_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const auditReasonDetails = store.select(preAuditReviewQuery.selectPreAuditReviewDetails)()?.auditReasonDetails;

    return fb.group<PreAuditReviewAuditReasonFormModel>({
      reasonsForAudit: fb.control(auditReasonDetails?.reasonsForAudit ?? [], [
        GovukValidators.required('Select at least one reason for the audit'),
      ]),
      comment: fb.control(auditReasonDetails?.comment ?? null, {
        validators: [GovukValidators.required('Enter a comment')],
      }),
    });
  },
};
