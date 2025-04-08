import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';

import { RequestsService } from 'cca-api';

@Component({
  selector: 'cca-new-subsistence-fees-payment-request',
  templateUrl: './new-subsistence-fees-payment-request.component.html',
  standalone: true,
  imports: [PageHeadingComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewSubsistenceFeesPaymentRequestComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestsService = inject(RequestsService);

  onCreateNewPaymentRequest() {
    this.requestsService
      .processRequestCreateAction(
        {
          requestCreateActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
          requestType: 'SUBSISTENCE_FEES_RUN',
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
          const errorCode = errorData.requests && errorData.valid === false ? 'inProgress' : 'invalidChargeDate';

          this.router.navigate(['..', 'request-error'], {
            relativeTo: this.activatedRoute,
            queryParams: { errorCode },
          });
        },
      });
  }
}
