import { Component, inject, OnInit } from '@angular/core';

import { BreadcrumbService } from '@netz/common/navigation';

@Component({
  selector: 'netz-base-success',
  template: '',
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
