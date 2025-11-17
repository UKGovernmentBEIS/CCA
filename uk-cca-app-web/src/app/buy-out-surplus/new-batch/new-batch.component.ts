import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  GovukTableColumn,
  TableComponent,
  TagComponent,
  WarningTextComponent,
} from '@netz/govuk-components';

import { BuyOutAndSurplusInfoService, RequestsService } from 'cca-api';

@Component({
  selector: 'cca-new-batch',
  templateUrl: './new-batch.component.html',
  imports: [
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    WarningTextComponent,
    TableComponent,
    TagComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewBatchComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestsService = inject(RequestsService);
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);

  protected readonly excludedAccounts = toSignal(
    this.buyOutAndSurplusInfoService.getExcludedAccountsForBuyOutSurplusRun('TP6'),
  );

  protected readonly excludedAccountsColumns: GovukTableColumn[] = [
    { field: 'businessId', header: 'TU ID' },
    { field: 'name', header: 'Operator name' },
    { field: 'status', header: 'Status', widthClass: 'govuk-!-width-one-quarter' },
  ];

  onCreateNewBatch() {
    this.requestsService
      .processRequestCreateAction(
        {
          requestType: 'BUY_OUT_SURPLUS_RUN',
          requestCreateActionPayload: {
            payloadType: 'BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD',
            targetPeriodType: 'TP6',
          } as any,
        },
        'ENGLAND',
      )
      .subscribe({
        next: (res) => {
          this.router.navigate(['..', 'confirmation'], {
            relativeTo: this.activatedRoute,
            replaceUrl: true,
            queryParams: { referenceCode: res.requestId },
          });
        },
        error: (error) => {
          const errorData = error.error.data[0];
          const errorCode = errorData.requests && errorData.valid === false ? 'inProgress' : 'unavailable';

          this.router.navigate(['..', 'request-error'], {
            relativeTo: this.activatedRoute,
            queryParams: { errorCode },
          });
        },
      });
  }
}
