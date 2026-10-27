import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { DateInputComponent, TextareaComponent, WarningTextComponent } from '@netz/govuk-components';
import {
  ErrorSummaryComponent,
  ErrorSummaryInfo,
  MultipleFileInputComponent,
  SummaryComponent,
  WizardStepComponent,
} from '@shared/components';
import { fileUtils } from '@shared/utils';

import { BuyOutSurplusTransactionUpdateControllerService, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { pendingFacilitiesCount } from '../../utils';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { CCA3_MARK_ALL_CONTENT, Cca3MarkAllStatus } from './cca3-mark-all-facilities.types';
import {
  CCA3_MARK_ALL_FACILITIES_FORM,
  Cca3MarkAllFacilitiesFormModel,
  Cca3MarkAllFacilitiesFormProvider,
  COMMENTS_MAX_LENGTH,
} from './cca3-mark-all-facilities-form.provider';
import { toCca3MarkAllFacilitiesSummaryData } from './cca3-mark-all-facilities-summary-data';

@Component({
  selector: 'cca-cca3-mark-all-facilities',
  templateUrl: './cca3-mark-all-facilities.component.html',
  providers: [Cca3MarkAllFacilitiesFormProvider],
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    SummaryComponent,
    ErrorSummaryComponent,
    TextareaComponent,
    DateInputComponent,
    MultipleFileInputComponent,
    WarningTextComponent,
  ],
})
export class Cca3MarkAllFacilitiesComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(Cca3TransactionDetailsStore);
  private readonly buyOutSurplusTransactionUpdateControllerService = inject(
    BuyOutSurplusTransactionUpdateControllerService,
  );

  readonly form = inject<Cca3MarkAllFacilitiesFormModel>(CCA3_MARK_ALL_FACILITIES_FORM);

  private readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');
  private readonly transactionDetails = signal<FacilityBuyOutSurplusTransactionDTO>(
    this.activatedRoute.snapshot.data.transactionDetails,
  );

  protected readonly status = signal<Cca3MarkAllStatus>(this.activatedRoute.snapshot.data.markAllStatus);
  protected readonly content = computed(() => CCA3_MARK_ALL_CONTENT[this.status()]);

  // the resolved transaction may have changed since the details page was loaded, e.g. when reached through the history
  protected readonly canMarkAll = computed(
    () =>
      this.transactionDetails()?.chargeType === this.content().chargeType &&
      pendingFacilitiesCount(this.transactionDetails()) > 0,
  );

  protected readonly summaryData = computed(() => toCca3MarkAllFacilitiesSummaryData(this.transactionDetails()));

  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  protected readonly commentsMaxLength = signal(COMMENTS_MAX_LENGTH);

  constructor() {
    if (!this.canMarkAll()) {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute, replaceUrl: true });
    }

    // a stale batch run error would otherwise be displayed next to the form error summary
    this.form.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => this.errorSummaryInfo.set(null));
  }

  onSubmit() {
    const { paymentDate, comments, evidenceFiles } = this.form.value;

    this.errorSummaryInfo.set(null);

    this.buyOutSurplusTransactionUpdateControllerService
      .markAllBuyOutSurplusTransactionDetailsPaymentStatus(this.transactionId, {
        status: this.status(),
        paymentDate,
        comments,
        evidenceFiles: fileUtils.toAttachments(evidenceFiles),
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
        // the selected facilities may no longer be in the status they were selected with
        this.store.clearSelectedFacilities();
        this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
