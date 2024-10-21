import { NgComponentOutlet, NgForOf, NgIf } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  Inject,
  Injector,
  runInInjectionContext,
  Signal,
  Type,
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
  standalone: true,
  imports: [NgIf, NgComponentOutlet, NgForOf, PageHeadingComponent, TaskListComponent, GovukDatePipe],
  templateUrl: './request-action-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestActionPageComponent {
  vm: Signal<ViewModel> = computed(() => {
    const requestAction = this.store.select(requestActionQuery.selectAction)();
    if (!requestAction) {
      return null;
    }

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

  constructor(
    @Inject(REQUEST_ACTION_PAGE_CONTENT) private readonly contentFactoryMap: RequestActionPageContentFactoryMap,
    private readonly store: RequestActionStore,
    private readonly injector: Injector,
  ) {}
}
