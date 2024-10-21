import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { UnderlyingAgreementWaitReviewComponent } from './underlying-agreement-wait-review.component';

export const underlyingAgreementWaitReviewTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Application for underlying agreement sent for review',
    contentComponent: UnderlyingAgreementWaitReviewComponent,
  };
};
