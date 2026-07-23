import { NgComponentOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  Injector,
  runInInjectionContext,
  Signal,
  Type,
  ViewEncapsulation,
} from '@angular/core';

import {
  PageHeadingComponent,
  RelatedActionsComponent,
  RelatedTasksComponent,
  TaskHeaderInfoComponent,
  TaskListComponent,
  TimelineComponent,
  TimelineItemComponent,
} from '@netz/common/components';
import { TaskSection } from '@netz/common/model';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { ItemDTO, RequestActionInfoDTO, RequestTaskDTO, RequestTaskItemDTO, RequestTaskPayload } from 'cca-api';

import { REQUEST_TASK_PAGE_CONTENT } from '../../request-task.providers';
import { RequestTaskPageContentFactoryMap } from '../../request-task.types';

type ViewModel = {
  requestTask: RequestTaskDTO;
  requestType?: string;
  payload?: RequestTaskPayload;
  header: string;
  sections?: TaskSection[] | null;
  preContentComponent?: Type<unknown> | null;
  contentComponent?: Type<unknown> | null;
  postContentComponent?: Type<unknown> | null;
  relatedTasks: ItemDTO[];
  hasRelatedTasks: boolean;
  timeline: RequestActionInfoDTO[];
  hasTimeline: boolean;
  showAssignAction?: boolean;
  relatedActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  hasRelatedActions: boolean;
};

@Component({
  selector: 'netz-request-task-page',
  templateUrl: './request-task-page.component.html',
  imports: [
    PageHeadingComponent,
    TaskHeaderInfoComponent,
    NgComponentOutlet,
    RelatedTasksComponent,
    TimelineComponent,
    TimelineItemComponent,
    RelatedActionsComponent,
    TaskListComponent,
    TimelineItemLinkPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class RequestTaskPageComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly injector = inject(Injector);
  private readonly contentFactoryMap = inject<RequestTaskPageContentFactoryMap>(REQUEST_TASK_PAGE_CONTENT);

  private readonly requestTask = this.store.select(requestTaskQuery.selectRequestTask);
  private readonly payload = this.store.select(requestTaskQuery.selectRequestTaskPayload);
  private readonly relatedTasks = this.store.select(requestTaskQuery.selectRelatedTasks);
  private readonly timeline = this.store.select(requestTaskQuery.selectTimeline);
  private readonly showAssignAction = this.store.select(requestTaskQuery.selectIsAssignActionVisible);
  private readonly relatedActions = this.store.select(requestTaskQuery.selectRelatedActions);
  private readonly requestType = this.store.select(requestTaskQuery.selectRequestType);

  vm: Signal<ViewModel | null> = computed(() => {
    const requestTask = this.requestTask();
    if (!requestTask) return null;

    const payload = this.payload();
    const relatedTasks = this.relatedTasks();
    const timeline = this.timeline();
    const showAssignAction = this.showAssignAction();
    const relatedActions = this.relatedActions();
    const requestType = this.requestType();

    const { header, sections, preContentComponent, contentComponent, postContentComponent, hideRelatedActions } =
      runInInjectionContext(this.injector, () => this.contentFactoryMap[requestTask.type as string]());

    return {
      requestTask,
      requestType,
      payload,
      header,
      sections,
      preContentComponent,
      contentComponent,
      postContentComponent,
      relatedTasks,
      timeline,
      relatedActions,
      showAssignAction,
      hasRelatedTasks: relatedTasks?.length > 0,
      hasTimeline: timeline?.length > 0,
      hasRelatedActions: !hideRelatedActions && ((relatedActions?.length ?? 0) > 0 || !!showAssignAction),
    };
  });

  constructor() {
    effect(() => {
      const viewModel = this.vm();
      if (!!viewModel && !viewModel.contentComponent && !viewModel.sections) {
        throw new Error(
          'You need to provide either a content component or the sections for the request task page to work',
        );
      }
    });
  }
}
