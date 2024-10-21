import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { UnderlyingAgreementVariationWaitActivationComponent } from './underlying-agreement-variation-wait-activation.component';

export const underlyingAgreementVariationWaitActivationTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: 'Application to vary underlying agreement sent for review',
    contentComponent: UnderlyingAgreementVariationWaitActivationComponent,
  };
};
