import { TaskItem, TaskSection } from '@netz/common/model';

import { TaskItemStatus } from '../task-item-status';

/**
 * Determines wheather the subtask links are clickable depending on the isEditable argument
 */
export function filterEditableTaskLinks(sections: TaskSection[], isEditable: boolean): TaskSection[] {
  if (isEditable) return sections;
  return sections.map((s) => ({
    ...s,
    tasks: s.tasks.map(filterTaskLinks),
  }));
}
const filterTaskLinks = (task: TaskItem): TaskItem =>
  task.status === TaskItemStatus.NOT_STARTED ? { ...task, link: '' } : task;

export const boolToString = (bool: boolean): 'Yes' | 'No' | null =>
  typeof bool !== 'boolean' ? null : bool ? 'Yes' : 'No';
