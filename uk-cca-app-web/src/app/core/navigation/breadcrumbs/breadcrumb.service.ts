import { Inject, Injectable, WritableSignal } from '@angular/core';

import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs/index';

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  constructor(@Inject(BREADCRUMB_ITEMS) readonly breadcrumbItem: WritableSignal<BreadcrumbItem[]>) {}

  show(items: BreadcrumbItem[]): void {
    this.breadcrumbItem.set(items);
  }

  showDashboardBreadcrumb(): void {
    this.breadcrumbItem.set([
      {
        text: 'Dashboard',
        link: ['dashboard'],
      },
    ]);
  }

  clear(): void {
    this.breadcrumbItem.set(null);
  }
}
