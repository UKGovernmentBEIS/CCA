import { InjectionToken } from '@angular/core';

import { PayloadMutator } from './payload-mutator';

export const PAYLOAD_MUTATORS = new InjectionToken<PayloadMutator[]>('Payload mutators');
