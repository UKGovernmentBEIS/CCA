import { InjectionToken } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

export type TaskStatusTagMap = Record<string, { text: string; color: TagColor }>;

export const TASK_STATUS_TAG_MAP = new InjectionToken<TaskStatusTagMap>('Task status tag map');
