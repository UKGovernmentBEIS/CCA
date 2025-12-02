import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { TimelineComponent } from '@netz/common/components';
import { TimelineItemComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { StatusColorPipe, WorkflowTypePipe } from '@shared/pipes';
import { StatusPipe } from '@shared/pipes';

import { ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

type WorkflowDetailsViewModel = {
  workflowDetails: RequestDetailsDTO;
  requestItems: ItemDTOResponse;
  requestActions: RequestActionInfoDTO[];
};

@Component({
  selector: 'cca-workflow-details',
  templateUrl: './workflow-details.component.html',
  imports: [
    StatusPipe,
    TagComponent,
    PageHeadingComponent,
    TabsComponent,
    TimelineItemLinkPipe,
    WorkflowTypePipe,
    TabLazyDirective,
    StatusColorPipe,
    TimelineComponent,
    TimelineItemComponent,
    // WorkflowNotesComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authStore = inject(AuthStore);
  private readonly roleType = this.authStore.select(selectUserRoleType);

  private readonly data = this.activatedRoute.snapshot.data['workflowDetailsItemsAndActions'];
  protected readonly navigationState = { returnUrl: this.router.url };

  protected readonly details: WorkflowDetailsViewModel = {
    workflowDetails: this.data.workflowDetails,
    requestItems: this.data.requestItems,
    requestActions: this.data.requestActions,
  };

  protected readonly userIsRegulator = computed(() => this.roleType() === 'REGULATOR');
}
