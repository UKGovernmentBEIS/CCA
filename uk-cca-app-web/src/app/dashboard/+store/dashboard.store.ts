import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';
import produce from 'immer';

import { ItemTargetUnitDTO } from 'cca-api';

import { DashboardState, initialState, WorkflowItemsAssignmentType } from './dashboard.state';

@Injectable({ providedIn: 'root' })
export class DashboardStore extends SignalStore<DashboardState> {
  constructor() {
    super(initialState);
  }

  setActiveTab(activeTab: WorkflowItemsAssignmentType) {
    this.setState(
      produce(this.state, (state) => {
        state.activeTab = activeTab;
      }),
    );
  }

  setItems(items: ItemTargetUnitDTO[]) {
    this.setState(
      produce(this.state, (state) => {
        state.items = items;
      }),
    );
  }

  setTotal(total: number) {
    this.setState(
      produce(this.state, (state) => {
        state.total = total;
      }),
    );
  }

  setPage(page: number) {
    this.setState(
      produce(this.state, (state) => {
        state.paging = { ...this.state.paging, page };
      }),
    );
  }
}
