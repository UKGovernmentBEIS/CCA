import { InjectionToken } from '@angular/core';

export const SUBTASK_TO_PROP_MAP = new InjectionToken<Record<string, string>>('Subtask to task property map');

export const TASK_SECTIONS_COMPLETED_PROP = new InjectionToken<string>(
  '' + '"*sectionsCompleted" property name for specific task type (e.g. "empSectionsCompleted")',
);
