import { TaskStatusTagMap } from '@netz/common/pipes';

import { TaskItemStatus } from './task-item-status';

export const taskStatusTagMap: TaskStatusTagMap = {
  [TaskItemStatus.NOT_STARTED]: {
    text: 'Not yet started',
    color: 'blue',
  },
  [TaskItemStatus.UNDECIDED]: {
    text: 'Undecided',
    color: 'blue',
  },
  [TaskItemStatus.COMPLETED]: {
    text: 'Completed',
    color: null,
  },
  [TaskItemStatus.CANNOT_START_YET]: {
    text: 'Cannot start yet',
    color: null,
  },
  [TaskItemStatus.IN_PROGRESS]: {
    text: 'In progress',
    color: 'light-blue',
  },
  [TaskItemStatus.ACCEPTED]: {
    text: 'Accepted',
    color: 'green',
  },
  [TaskItemStatus.REJECTED]: {
    text: 'Rejected',
    color: 'red',
  },
};
