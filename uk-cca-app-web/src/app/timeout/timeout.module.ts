import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';

import { TimedOutComponent } from './timed-out/timed-out.component';
import { TimeoutBannerComponent } from './timeout-banner/timeout-banner.component';

@NgModule({
  imports: [CommonModule, PageHeadingComponent, SharedModule, TimedOutComponent, TimeoutBannerComponent],
  exports: [TimedOutComponent, TimeoutBannerComponent],
})
export class TimeoutModule {}
