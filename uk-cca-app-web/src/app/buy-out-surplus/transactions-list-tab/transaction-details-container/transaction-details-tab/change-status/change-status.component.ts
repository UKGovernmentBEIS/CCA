import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import {
  ConditionalContentDirective,
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import {
  ErrorSummaryComponent,
  ErrorSummaryInfo,
  MultipleFileInputComponent,
  SummaryComponent,
  WizardStepComponent,
} from '@shared/components';
import { TerminatedTransactionComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { AccountBuyOutSurplusTransactionDTO, BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import {
  CHANGE_STATUS_FORM,
  ChangeStatusFormModel,
  ChangeStatusFormProvider,
  COMMENTS_MAX_LENGTH,
} from './change-status-form.provider';
import { toChangeStatusSummaryData } from './change-status-summary-data';

export const BATCH_RUN_IN_PROGRESS_MESSAGE = 'You cannot make an adjustment while a Batch Run is in progress.';

@Component({
  selector: 'cca-change-status',
  templateUrl: './change-status.component.html',
  providers: [ChangeStatusFormProvider],
  imports: [
    SummaryComponent,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    TextareaComponent,
    ConditionalContentDirective,
    MultipleFileInputComponent,
    DateInputComponent,
    ErrorSummaryComponent,
    TerminatedTransactionComponent,
  ],
})
export class ChangeStatusComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutSurplusTransactionUpdateControllerService = inject(
    BuyOutSurplusTransactionUpdateControllerService,
  );

  readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');

  protected readonly transactionDetails = this.activatedRoute.snapshot.data
    .transactionDetails as AccountBuyOutSurplusTransactionDTO;

  protected readonly transactionSummary = toChangeStatusSummaryData(this.transactionDetails);

  readonly form = inject<ChangeStatusFormModel>(CHANGE_STATUS_FORM);

  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  protected readonly commentsMaxLength = COMMENTS_MAX_LENGTH;

  onSubmit() {
    this.errorSummaryInfo.set(null);

    const status = this.form.value.status;
    let comments = '';

    switch (status) {
      case 'AWAITING_PAYMENT':
        comments = this.form.value.awaitingPaymentComments;
        break;

      case 'UNDER_APPEAL':
        comments = this.form.value.appealComments;
        break;

      case 'NOT_REQUIRED':
        comments = this.form.value.notRequiredComments;
        break;

      case 'AWAITING_REFUND':
        comments = this.form.value.awaitingRefundComments;
        break;

      case 'PAID':
        comments = this.form.value.paidComments;
        break;

      case 'REFUNDED':
        comments = this.form.value.refundedComments;
        break;
    }

    this.buyOutSurplusTransactionUpdateControllerService
      .updateBuyOutSurplusTransactionDetailsPaymentStatus(this.transactionId, {
        status: status,
        evidenceFiles: fileUtils.toAttachments(this.form.value.evidenceFiles),
        comments: comments,
        paymentDate: this.form.value.paymentDate,
        dateOfAppeal: this.form.value.dateOfAppeal,
        detailIds: [this.transactionDetails.detailId],
      })
      .pipe(
        catchBadRequest(ErrorCodes.BS1004, () => {
          this.errorSummaryInfo.set({ message: BATCH_RUN_IN_PROGRESS_MESSAGE });

          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
