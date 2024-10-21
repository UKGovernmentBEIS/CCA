import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { UnderlyingAgreementVariationWaitReviewComponent } from './underlying-agreement-variation-wait-review.component';

export const underlyingAgreementVariationWaitReviewTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Application for underlying agreement variation sent for review',
    contentComponent: UnderlyingAgreementVariationWaitReviewComponent,
  };
};
