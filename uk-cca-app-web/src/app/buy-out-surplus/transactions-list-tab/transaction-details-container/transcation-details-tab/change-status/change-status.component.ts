import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  ConditionalContentDirective,
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import { MultipleFileInputComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { TerminatedTransactionComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { BuyOutSurplusTransactionDetailsDTO, BuyOutSurplusTransactionUpdateControllerService } from 'cca-api';

import { CHANGE_STATUS_FORM, ChangeStatusFormModel, ChangeStatusFormProvider } from './change-status-form.provider';
import { toChangeStatusSummaryData } from './change-status-summary-data';

@Component({
  selector: 'cca-change-status',
  templateUrl: './change-status.component.html',
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
    TerminatedTransactionComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ChangeStatusFormProvider],
})
export class ChangeStatusComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutSurplusTransactionUpdateControllerService = inject(
    BuyOutSurplusTransactionUpdateControllerService,
  );

  readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');

  protected readonly transactionDetails = this.activatedRoute.snapshot.data
    .transactionDetails as BuyOutSurplusTransactionDetailsDTO;

  protected readonly transactionSummary = toChangeStatusSummaryData(this.transactionDetails);

  readonly form = inject<ChangeStatusFormModel>(CHANGE_STATUS_FORM);

  onSubmit() {
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
      .updateBuyOutSurplusTransactionPaymentStatus(this.transactionId, {
        status: status,
        evidenceFiles: fileUtils.toAttachments(this.form.value.evidenceFiles),
        comments: comments,
        paymentDate: this.form.value.paymentDate,
      })
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
