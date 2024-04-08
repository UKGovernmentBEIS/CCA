import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';
import { SharedModule } from '@shared/shared.module';

import { CustomReportComponent } from './custom/custom.component';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsRoutingModule } from './mi-reports-routing.module';

@NgModule({
  declarations: [CustomReportComponent, MiReportsComponent],
  imports: [MiReportsRoutingModule, PageHeadingComponent, PendingButtonDirective, RouterModule, SharedModule],
})
export class MiReportsModule {}
