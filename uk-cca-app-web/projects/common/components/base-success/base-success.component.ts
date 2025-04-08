import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';

import { BreadcrumbService } from '@netz/common/navigation';

@Component({
  selector: 'netz-base-success',
  standalone: true,
  template: '',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaseSuccessComponent implements OnInit {
  protected readonly breadcrumbs = inject(BreadcrumbService);

  ngOnInit(): void {
    this.breadcrumbs.show([
      {
        text: 'Dashboard',
        link: ['dashboard'],
      },
    ]);
  }
}
