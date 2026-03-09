import { NgComponentOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  Injector,
  runInInjectionContext,
  Signal,
  Type,
  inject,
} from '@angular/core';

import { PageHeadingComponent, TaskListComponent } from '@netz/common/components';
import { TaskSection } from '@netz/common/model';
import { GovukDatePipe } from '@netz/common/pipes';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';

import { RequestActionDTO } from 'cca-api';

import { REQUEST_ACTION_PAGE_CONTENT } from '../../request-action.providers';
import { RequestActionPageContentFactoryMap } from '../../request-action.types';

type ViewModel = {
  requestAction: RequestActionDTO;
  header: string;
  sections: TaskSection[];
  component: Type<unknown>;
};

@Component({
  selector: 'netz-request-action-page',
  imports: [NgComponentOutlet, PageHeadingComponent, TaskListComponent, GovukDatePipe],
  templateUrl: './request-action-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestActionPageComponent {
  private readonly contentFactoryMap = inject<RequestActionPageContentFactoryMap>(REQUEST_ACTION_PAGE_CONTENT);
  private readonly store = inject(RequestActionStore);
  private readonly injector = inject(Injector);

  vm: Signal<ViewModel> = computed(() => {
    const requestAction = this.store.select(requestActionQuery.selectAction)();
    if (!requestAction) return null;

    const { header, sections, component } = runInInjectionContext(this.injector, () =>
      this.contentFactoryMap[requestAction.type](),
    );

    return {
      requestAction,
      header,
      sections,
      component,
    };
  });
}
