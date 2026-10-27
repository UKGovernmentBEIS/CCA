import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { toTransactionDetails } from './cca3-transaction-details-data';
import { Cca3TransactionDetailsFiltersComponent } from './cca3-transaction-details-filters/cca3-transaction-details-filters.component';
import { Cca3TransactionDetailsTableComponent } from './cca3-transaction-details-table/cca3-transaction-details-table.component';

@Component({
  selector: 'cca-cca3-transaction-details',
  template: `
    @if (transactionDetails(); as transactionDetails) {
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <netz-page-heading size="xl" data-testid="page-heading" class="govuk-tag-heading-align">
            {{ transactionDetails.transactionCode }}

            <span>
              <govuk-tag [color]="transactionDetails.paymentStatus | statusColor" class="tag-width">
                {{ transactionDetails.paymentStatus | status }}
              </govuk-tag>
            </span>
          </netz-page-heading>
        </div>
      </div>

      <cca-summary [data]="data()" />

      <cca-cca3-transaction-details-filters />
      <cca-cca3-transaction-details-table />
    }
  `,
  imports: [
    TagComponent,
    PageHeadingComponent,
    StatusPipe,
    StatusColorPipe,
    SummaryComponent,
    Cca3TransactionDetailsFiltersComponent,
    Cca3TransactionDetailsTableComponent,
  ],
})
export class Cca3TransactionDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly userRoleType = inject(AuthStore).select(selectUserRoleType);

  protected readonly transactionDetails = toSignal(
    this.activatedRoute.data.pipe(
      map((data) => data['transactionDetails'] as FacilityBuyOutSurplusTransactionDTO | undefined),
    ),
  );

  protected readonly data = computed(() =>
    toTransactionDetails(this.transactionDetails(), this.userRoleType() === 'REGULATOR'),
  );
}
