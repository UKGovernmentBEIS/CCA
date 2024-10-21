import { InjectionToken } from '@angular/core';

import { SideEffect } from './side-effect';

export const SIDE_EFFECTS = new InjectionToken<SideEffect[]>('Side effect');
