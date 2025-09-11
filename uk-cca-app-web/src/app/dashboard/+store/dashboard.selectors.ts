import { createDescendingSelector, createSelector, StateSelector } from '@netz/common/store';

import { ItemDTO } from 'cca-api';

import { DashboardState, Paging, WorkflowItemsAssignmentType } from './dashboard.state';

export const selectActiveTab: StateSelector<DashboardState, WorkflowItemsAssignmentType> = createSelector(
  (state) => state.activeTab,
);

export const selectItems: StateSelector<DashboardState, ItemDTO[]> = createSelector((state) => state.items);

export const selectTotal: StateSelector<DashboardState, number> = createSelector((state) => state.totalItems);

export const selectPaging: StateSelector<DashboardState, Paging> = createSelector((state) => state.paging);

export const selectPage: StateSelector<DashboardState, number> = createDescendingSelector(
  selectPaging,
  (state) => state.page,
);

export const selectPageSize: StateSelector<DashboardState, number> = createDescendingSelector(
  selectPaging,
  (state) => state.pageSize,
);
