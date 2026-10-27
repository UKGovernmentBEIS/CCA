import { inject, Service } from '@angular/core';

import { Observable, tap } from 'rxjs';

import { SignalStore } from '@netz/common/store';

import {
  BuyOutAndSurplusFacilityTransactionViewService,
  FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO,
  FacilityBuyOutSurplusTransactionDetailSearchResultsDTO,
  FacilityBuyOutSurplusTransactionDetailsSearchCriteria,
} from 'cca-api';

import { DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from '../../utils';

export type Cca3TransactionDetailsState = {
  transactionId: number;
  facilities: FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO[];
  selectedFacilities: Map<string, FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO>;
  totalFacilities: number;
  currentPage: number;
  pageSize: number;
  loaded: boolean;
};

const INITIAL_STATE: Cca3TransactionDetailsState = {
  transactionId: null,
  facilities: [],
  selectedFacilities: new Map(),
  totalFacilities: 0,
  currentPage: DEFAULT_PAGE,
  pageSize: DEFAULT_PAGE_SIZE,
  loaded: false,
};

@Service({ autoProvided: false })
export class Cca3TransactionDetailsStore extends SignalStore<Cca3TransactionDetailsState> {
  private readonly facilityTransactionViewService = inject(BuyOutAndSurplusFacilityTransactionViewService);

  constructor() {
    super(INITIAL_STATE);
  }

  fetchFacilities(
    transactionId: number,
    criteria: FacilityBuyOutSurplusTransactionDetailsSearchCriteria,
  ): Observable<FacilityBuyOutSurplusTransactionDetailSearchResultsDTO> {
    // Changing the transaction means a different set of facilities, so any selection from the previous one is dropped.
    if (this.state.transactionId !== transactionId) {
      this.updateState({
        transactionId,
        facilities: [],
        selectedFacilities: new Map(),
        totalFacilities: 0,
        currentPage: DEFAULT_PAGE,
        pageSize: DEFAULT_PAGE_SIZE,
        loaded: false,
      });
    }

    return this.facilityTransactionViewService.getFacilityBuyOutSurplusTransactionDetails(transactionId, criteria).pipe(
      tap((response) =>
        this.updateState({
          facilities: response?.transactionDetails ?? [],
          totalFacilities: response?.total ?? 0,
          currentPage: criteria.pageNumber + 1,
          pageSize: criteria.pageSize,
          loaded: true,
        }),
      ),
    );
  }

  updateSelectedFacilities(selectedRow: FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO, checked: boolean) {
    if (selectedRow.paymentStatus === 'TERMINATED') return; // terminated facilities are not selectable

    const selectedFacilities = new Map(this.state.selectedFacilities);

    if (checked) {
      selectedFacilities.set(selectedRow.facilityBusinessId, selectedRow);
    } else {
      selectedFacilities.delete(selectedRow.facilityBusinessId);
    }

    this.updateState({ selectedFacilities });
  }

  clearSelectedFacilities() {
    this.updateState({ selectedFacilities: new Map() });
  }
}
