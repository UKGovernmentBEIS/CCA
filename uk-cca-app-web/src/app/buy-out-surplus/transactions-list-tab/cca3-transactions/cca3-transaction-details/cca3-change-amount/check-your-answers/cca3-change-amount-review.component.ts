import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';
import { SummaryComponent, SummaryFactory } from '@shared/components';
import { fileUtils, formatGbp } from '@shared/utils';

import { BuyOutSurplusTransactionDetailUpdateControllerService } from 'cca-api';

import { BATCH_AMOUNT_MESSAGE, Cca3ChangeAmountState, TERMINATED_AMOUNT_MESSAGE } from '../cca3-change-amount.state';

@Component({
  selector: 'cca-cca3-change-amount-review',
  template: `
    <netz-page-heading caption="Change current amount">Check your answers</netz-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <cca-summary [data]="state.summaryData()" />
        <cca-summary [data]="reviewData" />
      </div>
    </div>
    @if (!state.terminated()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm</button>
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent, PendingButtonDirective, ButtonDirective],
})
export class Cca3ChangeAmountReviewComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly api = inject(BuyOutSurplusTransactionDetailUpdateControllerService);
  readonly state = inject(Cca3ChangeAmountState);
  private readonly draft = this.state.form.getRawValue();

  protected readonly reviewData = new SummaryFactory()
    .addSection('', '..')
    .addRow('New current amount (GBP)', formatGbp(this.draft.amount), {
      change: true,
      appendChangeParam: false,
    })
    .addTextAreaRow('Comments', this.draft.comments, { change: true, appendChangeParam: false })
    .addFileListRow(
      'Uploaded evidence',
      fileUtils.toDownloadableFiles(
        fileUtils.toAttachments(this.draft.evidenceFiles),
        this.state.evidenceDownloadUrl(),
      ),
      { change: true, appendChangeParam: false },
    )
    .create();

  onSubmit() {
    if (!this.state.form.valid || this.state.terminated()) return;
    const { amount, comments, evidenceFiles } = this.state.form.getRawValue();
    this.state.errorSummary.set(null);

    this.api
      .updateBuyOutSurplusTransactionDetailAmount(this.state.facility().id, {
        amount,
        comments,
        evidenceFiles: fileUtils.toAttachments(evidenceFiles),
      })
      .pipe(
        catchBadRequest([ErrorCodes.BS1002, ErrorCodes.BS1004], (error) => {
          const terminated = error.error.code === ErrorCodes.BS1002;
          this.state.showError(terminated ? TERMINATED_AMOUNT_MESSAGE : BATCH_AMOUNT_MESSAGE, terminated);
          this.returnToForm();
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.state.resetDraft();
        this.router.navigate(['../confirmation'], { relativeTo: this.route, replaceUrl: true });
      });
  }

  private returnToForm() {
    this.router.navigate(['..'], { relativeTo: this.route, replaceUrl: true });
  }
}
