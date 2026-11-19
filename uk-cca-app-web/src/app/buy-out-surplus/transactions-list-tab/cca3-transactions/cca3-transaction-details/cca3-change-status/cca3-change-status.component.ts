import { Component, computed, inject, Signal, signal } from '@angular/core';
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
  WarningTextComponent,
} from '@netz/govuk-components';
import {
  ErrorSummaryComponent,
  ErrorSummaryInfo,
  MultipleFileInputComponent,
  SummaryComponent,
  WizardStepComponent,
} from '@shared/components';
import { fileUtils } from '@shared/utils';

import {
  BuyOutSurplusTransactionUpdateControllerService,
  BuyOutSurplusTransactionUpdatePaymentStatusDTO,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import {
  CCA3_CHANGE_STATUS_FORM,
  Cca3ChangeStatusFormModel,
  Cca3ChangeStatusFormProvider,
  COMMENTS_MAX_LENGTH,
} from './cca3-change-status-form.provider';
import { toCca3ChangeStatusSummaryData } from './cca3-change-status-summary-data';

type CommentsControlName = keyof Pick<
  Cca3ChangeStatusFormModel['controls'],
  | 'awaitingPaymentComments'
  | 'awaitingRefundComments'
  | 'appealComments'
  | 'notRequiredComments'
  | 'paidComments'
  | 'refundedComments'
>;

const COMMENTS_CONTROL_BY_STATUS: Partial<
  Record<BuyOutSurplusTransactionUpdatePaymentStatusDTO['status'], CommentsControlName>
> = {
  AWAITING_PAYMENT: 'awaitingPaymentComments',
  AWAITING_REFUND: 'awaitingRefundComments',
  UNDER_APPEAL: 'appealComments',
  NOT_REQUIRED: 'notRequiredComments',
  PAID: 'paidComments',
  REFUNDED: 'refundedComments',
};

@Component({
  selector: 'cca-cca3-change-status',
  templateUrl: './cca3-change-status.component.html',
  providers: [Cca3ChangeStatusFormProvider],
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    SummaryComponent,
    ErrorSummaryComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    TextareaComponent,
    DateInputComponent,
    MultipleFileInputComponent,
    WarningTextComponent,
  ],
})
export class Cca3ChangeStatusComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(Cca3TransactionDetailsStore);
  private readonly buyOutSurplusTransactionUpdateControllerService = inject(
    BuyOutSurplusTransactionUpdateControllerService,
  );

  readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');

  readonly form = inject<Cca3ChangeStatusFormModel>(CCA3_CHANGE_STATUS_FORM);

  protected readonly transactionDetails = signal(
    this.activatedRoute.snapshot.data.transactionDetails,
  ) as Signal<FacilityBuyOutSurplusTransactionDTO>;

  protected readonly chargeType = computed(() => this.transactionDetails().chargeType);

  private readonly selectedFacilities = this.store.select((state) => state.selectedFacilities);

  protected readonly summaryData = computed(() =>
    toCca3ChangeStatusSummaryData(this.transactionDetails().transactionCode, this.selectedFacilities().size),
  );

  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  protected readonly commentsMaxLength = COMMENTS_MAX_LENGTH;

  onSubmit() {
    const { status, paymentDate, dateOfAppeal, evidenceFiles } = this.form.value;
    const commentsControl = COMMENTS_CONTROL_BY_STATUS[status];

    this.errorSummaryInfo.set(null);

    this.buyOutSurplusTransactionUpdateControllerService
      .updateBuyOutSurplusTransactionDetailsPaymentStatus(this.transactionId, {
        status,
        comments: commentsControl ? this.form.value[commentsControl] : undefined,
        evidenceFiles: fileUtils.toAttachments(evidenceFiles),
        paymentDate,
        dateOfAppeal,
        detailIds: [...this.selectedFacilities().values()].map((facility) => facility.id),
      })
      .pipe(
        catchBadRequest(ErrorCodes.BS1004, () => {
          this.errorSummaryInfo.set({
            message: 'You cannot make any adjustments while the buy-out batch run is in progress',
          });

          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.store.clearSelectedFacilities();
        this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
