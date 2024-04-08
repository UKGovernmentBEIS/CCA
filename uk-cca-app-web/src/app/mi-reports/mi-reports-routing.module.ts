import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MiReportsListGuard } from './core/mi-reports-list.guard';
import { CustomReportComponent } from './custom/custom.component';
import { MiReportsComponent } from './mi-reports.component';

const routes: Routes = [
  {
    path: '',
    component: MiReportsComponent,
    canActivate: [MiReportsListGuard],
    resolve: { miReports: MiReportsListGuard },
  },
  {
    path: 'custom',
    data: { breadcrumb: 'Custom SQL report' },
    component: CustomReportComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MiReportsRoutingModule {}
