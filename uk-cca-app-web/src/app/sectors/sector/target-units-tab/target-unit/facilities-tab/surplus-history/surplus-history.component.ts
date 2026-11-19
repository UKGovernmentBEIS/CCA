import { Component, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

import { map, startWith, switchMap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  AccordionComponent,
  AccordionItemComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { BuyOutAndSurplusInfoService, FacilityInfoDTO } from 'cca-api';

import { SurplusAmountPipe } from '../surplus-amount.pipe';

@Component({
  selector: 'cca-facility-surplus-history',
  templateUrl: './surplus-history.component.html',
  imports: [
    PageHeadingComponent,
    GovukDatePipe,
    AccordionComponent,
    AccordionItemComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SurplusAmountPipe,
  ],
})
export default class FacilitySurplusHistoryComponent {
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);

  /** Resolved route data. */
  readonly facilityDetails = input.required<FacilityInfoDTO>();

  protected readonly surplusHistory = toSignal(
    toObservable(this.facilityDetails).pipe(
      switchMap((facilityDetails) =>
        this.buyOutAndSurplusInfoService.getFacilitySurplusHistory(facilityDetails.facilityId).pipe(
          map((history) =>
            [...history].sort((a, b) => {
              if (!a.submissionDate) return b.submissionDate ? 1 : 0;
              if (!b.submissionDate) return -1;
              return Date.parse(b.submissionDate) - Date.parse(a.submissionDate);
            }),
          ),
          startWith(undefined),
        ),
      ),
    ),
  );
}
