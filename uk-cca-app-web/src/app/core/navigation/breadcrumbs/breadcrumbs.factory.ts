import { InjectionToken, signal, WritableSignal } from '@angular/core';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs/breadcrumbs.interface';

export const BREADCRUMB_ITEMS = new InjectionToken<WritableSignal<BreadcrumbItem[]>>('Breadcrumb items', {
  providedIn: 'root',
  factory: () => signal([]),
});
