import { Injectable } from '@angular/core';

import { produce } from 'immer';

import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO, RequestTaskPayload } from 'cca-api';

import { SignalStore } from '../signal-store';
import { initialRequestTaskState, RequestTaskState } from './request-task.state';

@Injectable({ providedIn: 'root' })
export class RequestTaskStore extends SignalStore<RequestTaskState> {
  constructor() {
    super(initialRequestTaskState);
  }

  setRequestTaskItem(requestTaskItem: RequestTaskItemDTO) {
    this.setState(
      produce(this.state, (state) => {
        state.requestTaskItem = requestTaskItem;
      }),
    );
  }

  setRelatedTasks(relatedTasks: ItemDTO[]) {
    this.setState(
      produce(this.state, (state) => {
        state.relatedTasks = relatedTasks;
      }),
    );
  }

  setTimeline(timeline: RequestActionInfoDTO[]) {
    this.setState(
      produce(this.state, (state) => {
        state.timeline = timeline;
      }),
    );
  }

  setTaskReassignedTo(taskReassignedTo: string) {
    this.setState(
      produce(this.state, (state) => {
        state.taskReassignedTo = taskReassignedTo;
      }),
    );
  }

  setIsEditable(isEditable: boolean) {
    this.setState(
      produce(this.state, (state) => {
        state.isEditable = isEditable;
      }),
    );
  }

  setMetadata(metadata: Record<string, unknown>) {
    this.setState(
      produce(this.state, (state) => {
        state.metadata = metadata;
      }),
    );
  }

  setPayload<T extends RequestTaskPayload>(payload: T) {
    this.setState(
      produce(this.state, (state) => {
        state.requestTaskItem.requestTask.payload = payload;
      }),
    );
  }
}
