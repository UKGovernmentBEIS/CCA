import { AfterViewChecked, ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, TimelineComponent, TimelineItemComponent } from '@netz/common/components';
import { BreadcrumbService } from '@netz/common/navigation';
import { TimelineItemLinkPipe } from '@netz/common/pipes';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { PaymentRequestProcessStatusPipe, PaymentRequestStatusTagColorPipe } from '@shared/pipes';

import { WorkflowHistoryDetailsResponse } from './workflow-history.resolver';

@Component({
  selector: 'cca-workflow-history',
  templateUrl: './workflow-history.component.html',
  standalone: true,
  imports: [
    TagComponent,
    PageHeadingComponent,
    TabsComponent,
    TimelineItemLinkPipe,
    TabLazyDirective,
    PaymentRequestProcessStatusPipe,
    PaymentRequestStatusTagColorPipe,
    TimelineComponent,
    TimelineItemComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkflowHistoryComponent implements AfterViewChecked {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly breadcrumbService = inject(BreadcrumbService);

  readonly details = this.activatedRoute.snapshot.data.details as WorkflowHistoryDetailsResponse;
  readonly navigationState = { returnUrl: this.router.url };

  ngAfterViewChecked(): void {
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Subsistence fees',
        link: ['/', 'subsistence-fees'],
        fragment: 'workflow-history',
      },
    ]);
  }
}
