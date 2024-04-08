import { RequestTaskState } from '../+state';

export const mockRequestTask: RequestTaskState = {
  requestTaskItem: {
    allowedRequestTaskActions: [],
    requestInfo: {
      accountId: 1,
      competentAuthority: 'ENGLAND',
    },
    requestTask: {
      assignable: true,
      assigneeFullName: 'John Doe',
      assigneeUserId: 'f2ee3282-6e27-42a3-9217-464c03fd3d38',
      id: 2,
    },
  },
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: true,
};
