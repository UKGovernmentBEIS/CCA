import { AsyncPipe, NgComponentOutlet, NgFor, NgIf } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  Inject,
  Injector,
  runInInjectionContext,
  Signal,
  Type,
  ViewEncapsulation,
} from '@angular/core';
import { RouterLink } from '@angular/router';

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

import { ItemDTO, RequestActionInfoDTO, RequestTaskDTO, RequestTaskItemDTO } from 'cca-api';

import { REQUEST_TASK_PAGE_CONTENT } from '../../request-task.providers';
import { RequestTaskPageContentFactoryMap } from '../../request-task.types';

type ViewModel = {
  requestTask: RequestTaskDTO;
  header: string;
  sections: TaskSection[] | null;
  contentComponent: Type<unknown> | null;
  preContentComponent: Type<unknown> | null;
  postContentComponent: Type<unknown> | null;
  relatedTasks: ItemDTO[];
  hasRelatedTasks: boolean;
  timeline: RequestActionInfoDTO[];
  hasTimeline: boolean;
  showAssignAction: boolean;
  relatedActions: RequestTaskItemDTO['allowedRequestTaskActions'];
  hasRelatedActions: boolean;
};

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'netz-request-task-page',
  templateUrl: './request-task-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  imports: [
    RouterLink,
    PageHeadingComponent,
    NgIf,
    AsyncPipe,
    TaskHeaderInfoComponent,
    NgComponentOutlet,
    RelatedTasksComponent,
    TimelineComponent,
    TimelineItemComponent,
    NgFor,
    TimelineItemLinkPipe,
    RelatedActionsComponent,
    TaskListComponent,
  ],
})
export class RequestTaskPageComponent {
  vm: Signal<ViewModel> = computed(() => {
    const requestTask = this.store.select(requestTaskQuery.selectRequestTask)();
    if (!requestTask) {
      return null;
    }

    const relatedTasks = this.store.select(requestTaskQuery.selectRelatedTasks)();
    const timeline = this.store.select(requestTaskQuery.selectTimeline)();
    const showAssignAction = this.store.select(requestTaskQuery.selectIsAssignActionVisible)();
    const relatedActions = this.store.select(requestTaskQuery.selectRelatedActions)();
    const { header, sections, contentComponent, preContentComponent, postContentComponent } = runInInjectionContext(
      this.injector,
      () => this.contentFactoryMap[requestTask.type](),
    );

    return {
      requestTask,
      header,
      sections,
      contentComponent,
      preContentComponent,
      postContentComponent,
      relatedTasks,
      timeline,
      relatedActions,
      showAssignAction,
      hasRelatedTasks: relatedTasks?.length > 0,
      hasTimeline: timeline?.length > 0,
      hasRelatedActions: relatedActions?.length > 0 || showAssignAction,
    };
  });

  constructor(
    private readonly store: RequestTaskStore,
    @Inject(REQUEST_TASK_PAGE_CONTENT)
    private readonly contentFactoryMap: RequestTaskPageContentFactoryMap,
    protected readonly injector: Injector,
  ) {
    effect(() => {
      if (!!this.vm() && !this.vm().contentComponent && !this.vm().sections) {
        throw new Error(
          'You need to provide either a content component or the sections for the request task page to work',
        );
      }
    });
  }
}
