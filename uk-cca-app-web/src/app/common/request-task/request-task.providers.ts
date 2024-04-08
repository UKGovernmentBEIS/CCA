import { InjectionToken } from '@angular/core';

import { RequestTaskIsEditableResolver, RequestTaskPageContentFactoryMap } from './request-task.types';

/**
 * @description
 * A map object whose keys are request task types and values are of type {@link RequestTaskPageContentFactory}
 * This is used to resolve the task page's content (sections, custom components etc.).
 * The factory function may optionally be passed an `injector` argument if it needs to use specific providers
 *
 * @see {RequestTaskPageContentFactoryMap}
 */
export const REQUEST_TASK_PAGE_CONTENT = new InjectionToken<RequestTaskPageContentFactoryMap>(
  'Request task page content',
);

/**
 * @description
 * An token used to customize the resolution of whether a task is editable or not.
 *
 * @see {RequestTaskIsEditableResolver}
 */
export const REQUEST_TASK_IS_EDITABLE_RESOLVER = new InjectionToken<RequestTaskIsEditableResolver>(
  'Request task isEditable resolver',
);
