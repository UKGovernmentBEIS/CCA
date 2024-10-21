import { InjectionToken } from '@angular/core';

import { WizardFlowManager } from './wizard-flow-manager';

export const WIZARD_FLOW_MANAGERS = new InjectionToken<WizardFlowManager[]>('Wizard flow managers');
