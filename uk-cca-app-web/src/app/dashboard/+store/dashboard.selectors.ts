import { createDescendingSelector, createSelector, StateSelector } from '@netz/common/store';
import { Paging } from '@shared/components';

import { ItemDTO } from 'cca-api';

import { DashboardState, WorkflowItemsAssignmentType } from './dashboard.state';

export const selectActiveTab: StateSelector<DashboardState, WorkflowItemsAssignmentType> = createSelector(
  (state) => state.activeTab,
);

export const selectItems: StateSelector<DashboardState, ItemDTO[]> = createSelector((state) => state.items);

export const selectTotal: StateSelector<DashboardState, number> = createSelector((state) => state.total);

export const selectPaging: StateSelector<DashboardState, Paging> = createSelector((state) => state.paging);

export const selectPage: StateSelector<DashboardState, number> = createDescendingSelector(
  selectPaging,
  (state) => state.page,
);

export const selectPageSize: StateSelector<DashboardState, number> = createDescendingSelector(
  selectPaging,
  (state) => state.pageSize,
);
