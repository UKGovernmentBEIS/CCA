import { RequestTaskPageContentFactory } from '@netz/common/request-task';

import { UnderlyingAgreementWaitActivationComponent } from './underlying-agreement-wait-activation.component';

export const underlyingAgreementWaitActivationTaskContent: RequestTaskPageContentFactory = () => {
  return {
    header: `Application for underlying agreement awaiting operator's assent/activation`,
    contentComponent: UnderlyingAgreementWaitActivationComponent,
  };
};
