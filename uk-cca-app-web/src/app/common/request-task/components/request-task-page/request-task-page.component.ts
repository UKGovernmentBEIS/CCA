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

import { RelatedActionsComponent } from '@shared/components/related-actions/related-actions.component';
import { RelatedTasksComponent } from '@shared/components/related-tasks/related-tasks.component';
import { TaskHeaderInfoComponent } from '@shared/components/task-header-info/task-header-info.component';
import { TimelineComponent } from '@shared/components/timeline/timeline.component';
import { TimelineItemComponent } from '@shared/components/timeline/timeline-item.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { TimelineItemLinkPipe } from '@shared/pipes/timeline-item-link.pipe';

import { ItemDTO, RequestActionInfoDTO, RequestTaskDTO, RequestTaskItemDTO } from 'cca-api';

import { TaskListComponent } from '../../../shared/components/task-list';
import { TaskSection } from '../../../shared/model';
import { requestTaskQuery, RequestTaskStore } from '../../+state';
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
  selector: 'cca-request-task-page',
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
    private readonly injector: Injector,
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
