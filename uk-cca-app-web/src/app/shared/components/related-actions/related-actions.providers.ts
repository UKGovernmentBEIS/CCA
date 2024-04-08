import { InjectionToken } from '@angular/core';

import { RequestTaskDTO } from 'cca-api';

export type RelatedActionPath = string[] | ((taskId: RequestTaskDTO['id']) => string[]);
export type RelatedActionsMap = Record<string, { text: string; path: RelatedActionPath }>;

export const TASK_RELATED_ACTIONS_MAP = new InjectionToken<RelatedActionsMap>('Task related actions map');
