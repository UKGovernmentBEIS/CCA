import { TaskSection } from '@common/shared/model/task-list';

import { tasks } from './tasks';

export const sections: TaskSection[] = [
  {
    title: 'Some section A',
    tasks: tasks.slice(0, 1),
  },
  {
    title: 'Some section B',
    tasks: tasks.slice(1, 4),
  },
  {
    title: 'Some section C',
    tasks: tasks.slice(4, 6),
  },
];
