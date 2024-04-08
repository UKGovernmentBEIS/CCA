import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@core/navigation';

import { getHeadingMap } from '../../core/payment.map';
import { getPaymentBaseLink } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'cca-mark-paid',
  template: `
    <cca-page-heading>Are you sure you want to mark this payment as paid?</cca-page-heading>
    <div class="govuk-button-group" *ngIf="(store.isEditable$ | async) === true">
      <button (click)="onComplete()" ccaPendingButton govukButton type="button">Confirm and complete</button>
    </div>
    <cca-return-link
      [requestTaskType]="(store | async).requestTaskItem.requestTask.type"
      [requestMetadata]="(store | async).requestTaskItem.requestInfo.requestMetadata"
      returnLink="../details"
    ></cca-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkPaidComponent implements OnInit, OnDestroy {
  taskId$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.id),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.store.subscribe((state) => {
      this.breadcrumbService.show([
        {
          text: getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)[
            state.requestTaskItem?.requestTask.type
          ],
          link: [getPaymentBaseLink(state.requestType) + 'payment', state.requestTaskId, 'make', 'details'],
        },
      ]);
    });
  }

  onComplete(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postMarkAsPaid({
            ...state,
            completed: true,
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { method: 'BANK_TRANSFER' } }),
      );
  }

  ngOnDestroy(): void {
    this.breadcrumbService.clear();
  }
}
