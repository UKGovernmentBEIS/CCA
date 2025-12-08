import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, TimelineComponent, TimelineItemComponent } from '@netz/common/components';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { ConfigService } from '@shared/config';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { WorkflowHistoryDetailsResponse } from './workflow-history.resolver';

@Component({
  selector: 'cca-workflow-history',
  templateUrl: './workflow-history.component.html',
  imports: [
    TagComponent,
    PageHeadingComponent,
    TabsComponent,
    TimelineItemLinkPipe,
    TabLazyDirective,
    StatusPipe,
    StatusColorPipe,
    TimelineComponent,
    TimelineItemComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly configService = inject(ConfigService);

  protected readonly details = this.activatedRoute.snapshot.data.details as WorkflowHistoryDetailsResponse;
  protected readonly navigationState = { returnUrl: this.router.url };
  protected readonly showNotes = !this.configService.isFeatureEnabled('hideNotes');
}
