import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TimelineComponent } from '@netz/common/components';
import { RelatedTasksComponent, TimelineItemComponent } from '@netz/common/components';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { RequestStatusTagColorPipe } from '@shared/pipes';
import { RequestTypeToHeadingPipe } from '@shared/pipes';
import { WorkflowStatusPipe } from '@shared/pipes';

import { ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

type WorkflowDetailsViewModel = {
  workflowDetails: RequestDetailsDTO;
  requestItems: ItemDTOResponse;
  requestActions: RequestActionInfoDTO[];
};

@Component({
  selector: 'cca-workflow-details',
  standalone: true,
  templateUrl: './workflow-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WorkflowStatusPipe,
    TagComponent,
    PageHeadingComponent,
    TitleCasePipe,
    TabsComponent,
    TimelineItemLinkPipe,
    TabLazyDirective,
    RequestStatusTagColorPipe,
    RequestTypeToHeadingPipe,
    RelatedTasksComponent,
    TimelineComponent,
    TimelineItemComponent,
  ],
})
export class WorkflowDetailsComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly data = this.route.snapshot.data['workflowDetailsItemsAndActions'];
  readonly navigationState = { returnUrl: this.router.url };

  details: WorkflowDetailsViewModel = {
    workflowDetails: this.data.workflowDetails,
    requestItems: this.data.requestItems,
    requestActions: this.data.requestActions,
  };
}
