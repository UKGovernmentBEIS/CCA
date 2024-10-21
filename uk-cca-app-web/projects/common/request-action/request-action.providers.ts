import { InjectionToken } from '@angular/core';

import { RequestActionPageContentFactoryMap } from './request-action.types';

export const REQUEST_ACTION_PAGE_CONTENT = new InjectionToken<RequestActionPageContentFactoryMap>(
  'Request action page content',
);
