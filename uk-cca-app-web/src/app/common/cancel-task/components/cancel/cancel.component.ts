import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@common/request-task/+state';
import { BreadcrumbService } from '@core/navigation/breadcrumbs/breadcrumb.service';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';

import { GovukComponentsModule } from 'govuk-components';

import { cancelActionMap } from '../../cancel-action.map';

@Component({
  selector: 'cca-cancel-task',
  standalone: true,
  template: `
    <cca-page-heading size="xl"> Are you sure you want to cancel this task?</cca-page-heading>
    <p class="govuk-body">This task and its data will be deleted.</p>
    <div class="govuk-button-group">
      <button type="button" ccaPendingButton (click)="cancel()" govukWarnButton>Yes, cancel this task</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [GovukComponentsModule, PendingButtonDirective, PageHeadingComponent],
})
export class CancelComponent implements OnInit {
  private requestTaskItem$ = this.store.rxSelect(requestTaskQuery.selectRequestTaskItem);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly breadcrumbsService: BreadcrumbService,
    private readonly store: RequestTaskStore,
  ) {}

  ngOnInit() {
    this.resolveBreadcrumb();
    // check if cancel action is available for given request type
    this.requestTaskItem$
      .pipe(
        filter((rti) => !!rti),
        map((rti) => rti.requestTask.type),
        first(),
      )
      .subscribe((type) => {
        if (cancelActionMap[type] == null) {
          this.router.navigate(['error', '404']);
        }
      });
  }

  cancel(): void {
    // TODO
  }

  private resolveBreadcrumb(): void {
    const breadcrumbs = this.breadcrumbsService.breadcrumbItem();
    const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
    const parentRoute = this.route.routeConfig.path === '' ? this.route.parent.parent : this.route.parent;
    const parentRouteEndsWithTaskId = parentRoute.routeConfig.path.endsWith(':taskId');

    if (/\d+$/.test(lastBreadcrumb.link.join('/')) && !parentRouteEndsWithTaskId) {
      const parentRoutePath = parentRoute.routeConfig.path;
      lastBreadcrumb.link = [...lastBreadcrumb.link, parentRoutePath];
      this.breadcrumbsService.show([...breadcrumbs.slice(0, breadcrumbs.length - 1), lastBreadcrumb]);
    }
  }
}
