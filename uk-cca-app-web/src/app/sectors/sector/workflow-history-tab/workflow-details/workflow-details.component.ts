import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TimelineComponent } from '@netz/common/components';
import { TimelineItemComponent } from '@netz/common/components';
import { PageHeadingComponent } from '@netz/common/components';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { StatusColorPipe } from '@shared/pipes';
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
  standalone: true,
  imports: [
    StatusPipe,
    TagComponent,
    PageHeadingComponent,
    TabsComponent,
    TimelineItemLinkPipe,
    TabLazyDirective,
    StatusColorPipe,
    TimelineComponent,
    TimelineItemComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly data = this.activatedRoute.snapshot.data['workflowDetailsItemsAndActions'];
  protected readonly navigationState = { returnUrl: this.router.url };

  protected readonly details: WorkflowDetailsViewModel = {
    workflowDetails: this.data.workflowDetails,
    requestItems: this.data.requestItems,
    requestActions: this.data.requestActions,
  };
}
