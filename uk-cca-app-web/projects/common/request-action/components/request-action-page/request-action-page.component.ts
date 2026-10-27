import { NgComponentOutlet } from '@angular/common';
import { Component, computed, inject, Injector, runInInjectionContext, Signal, Type } from '@angular/core';

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
  sections?: TaskSection[];
  component?: Type<unknown>;
};

@Component({
  selector: 'netz-request-action-page',
  templateUrl: './request-action-page.component.html',
  imports: [NgComponentOutlet, PageHeadingComponent, TaskListComponent, GovukDatePipe],
})
export class RequestActionPageComponent {
  private readonly contentFactoryMap = inject<RequestActionPageContentFactoryMap>(REQUEST_ACTION_PAGE_CONTENT);
  private readonly store = inject(RequestActionStore);
  private readonly injector = inject(Injector);

  vm: Signal<ViewModel | null> = computed(() => {
    const requestAction = this.store.select(requestActionQuery.selectAction)();
    if (!requestAction) return null;

    const { header, sections, component } = runInInjectionContext(this.injector, () =>
      this.contentFactoryMap[requestAction.type as string](),
    );

    return {
      requestAction,
      header,
      sections,
      component,
    };
  });
}
