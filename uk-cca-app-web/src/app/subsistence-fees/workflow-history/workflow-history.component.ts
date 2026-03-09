import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent, TimelineComponent, TimelineItemComponent } from '@netz/common/components';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { WorkflowNotesComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { WorkflowHistoryDetailsResponse } from '../../buy-out-surplus/workflow-history/workflow-history.resolver';

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
    WorkflowNotesComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);

  private readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly details = this.activatedRoute.snapshot.data.details as WorkflowHistoryDetailsResponse;
  protected readonly navigationState = { returnUrl: this.router.url };

  protected readonly userIsRegulator = computed(() => this.roleType() === 'REGULATOR');
}
