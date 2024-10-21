import { Inject, Injectable } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BREADCRUMB_ITEMS } from './breadcrumbs.factory';
import { BreadcrumbItem } from './breadcrumbs.interface';

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  constructor(@Inject(BREADCRUMB_ITEMS) readonly breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>) {}

  show(items: BreadcrumbItem[]): void {
    this.breadcrumbItem$.next(items);
  }

  showDashboardBreadcrumb(): void {
    this.breadcrumbItem$.next([
      {
        text: 'Dashboard',
        link: ['dashboard'],
      },
    ]);
  }

  clear(): void {
    this.breadcrumbItem$.next(null);
  }
}
