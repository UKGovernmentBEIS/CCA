import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';
import { ErrorSummaryComponent, ErrorSummaryInfo, SummaryComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  BuyOutSurplusTransactionUpdateControllerService,
  BuyOutSurplusTransactionUpdateReceivedAmountDTO,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { NEGATIVE_RECEIVED_AMOUNT_MESSAGE } from '../cca3-received-amount.validators';
import { CCA3_RECEIVED_AMOUNT_FORM } from '../cca3-received-amount-form.provider';
import {
  signedAdjustment,
  toReceivedAmountReviewSummaryData,
  toReceivedAmountSummaryData,
} from '../cca3-received-amount-summary-data';

@Component({
  selector: 'cca-cca3-received-amount-review',
  template: `
    @if (errorSummary(); as error) {
      <div class="govuk-!-width-two-thirds">
        <cca-error-summary [errorSummaryInfo]="error" />
      </div>
    }

    <netz-page-heading caption="Change received amount">Check your answers</netz-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <cca-summary [data]="summaryData()" />
        <cca-summary [data]="reviewData()" />
      </div>
    </div>
    <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm</button>
  `,
  imports: [ErrorSummaryComponent, PageHeadingComponent, SummaryComponent, PendingButtonDirective, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3ReceivedAmountReviewComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly api = inject(BuyOutSurplusTransactionUpdateControllerService);

  readonly form = inject(CCA3_RECEIVED_AMOUNT_FORM);

  private readonly transaction = this.route.snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO;

  protected readonly summaryData = signal(toReceivedAmountSummaryData(this.transaction));

  protected readonly reviewData = signal(toReceivedAmountReviewSummaryData(this.transaction, this.form.getRawValue()));

  protected readonly errorSummary = signal<ErrorSummaryInfo | null>(null);

  onSubmit() {
    if (!this.form.valid) return;

    this.errorSummary.set(null);

    const draft = this.form.getRawValue();
    const { paymentDate, comments, evidenceFiles } = draft;
    const payload: BuyOutSurplusTransactionUpdateReceivedAmountDTO = {
      adjustmentAmount: signedAdjustment(draft).toFixed(2),
      ...(paymentDate ? { paymentDate: paymentDate.toISOString().slice(0, 10) } : {}),
      comments,
      evidenceFiles: fileUtils.toAttachments(evidenceFiles) ?? {},
    };

    this.api
      .updateBuyOutSurplusTransactionReceivedAmount(this.transaction.id, payload)
      .pipe(
        catchBadRequest([ErrorCodes.BS1004, ErrorCodes.BS1005, ErrorCodes.BS1006], (error) => {
          const messages = {
            [ErrorCodes.BS1004]: 'You cannot make an adjustment while a Batch Run is in progress.',
            [ErrorCodes.BS1005]: NEGATIVE_RECEIVED_AMOUNT_MESSAGE,
            [ErrorCodes.BS1006]: 'You can only update the received amount for a buy-out transaction.',
          };
          this.errorSummary.set({
            message: messages[error.error.code],
            link: '../..',
            linkText: `Return to: Buy-out MoA ${this.transaction.transactionCode}`,
          });

          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
