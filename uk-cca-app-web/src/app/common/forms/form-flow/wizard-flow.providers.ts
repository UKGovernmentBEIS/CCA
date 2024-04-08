import { InjectionToken } from '@angular/core';

import { WizardFlowManager } from '@common/forms/form-flow/wizard-flow-manager';

export const WIZARD_FLOW_MANAGERS = new InjectionToken<WizardFlowManager[]>('Step flow managers');
