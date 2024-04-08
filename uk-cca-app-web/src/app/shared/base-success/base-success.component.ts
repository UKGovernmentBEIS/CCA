import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';

import { BreadcrumbService } from '@core/navigation';

@Component({
  selector: 'cca-base-success',
  template: '',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaseSuccessComponent implements OnInit {
  protected readonly breadcrumbs = inject(BreadcrumbService);

  ngOnInit(): void {
    this.breadcrumbs.showDashboardBreadcrumb();
  }
}
