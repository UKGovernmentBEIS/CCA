import { inject, Injectable } from '@angular/core';

import { catchError } from 'rxjs';

import { SignalStore } from '@netz/common/store';

import {
  SubsistenceFeesMoAReceivedAmountControllerService,
  SubsistenceFeesMoaReceivedAmountDetailsDTO,
  SubsistenceFeesMoaReceivedAmountHistoryDTO,
} from 'cca-api';

export type SectorMoasReceivedAmountState = {
  transactionId: string;
  businessId: string;
  name: string;
  details: SubsistenceFeesMoaReceivedAmountDetailsDTO;
  currentTotalAmount?: string;
  receivedAmount?: string;
  receivedAmountHistoryList?: SubsistenceFeesMoaReceivedAmountHistoryDTO[];
  changeType: 'add' | 'subtract';
};

const INITIAL_STATE: SectorMoasReceivedAmountState = {
  transactionId: null,
  businessId: null,
  name: null,
  details: null,
  currentTotalAmount: null,
  receivedAmount: null,
  receivedAmountHistoryList: [],
  changeType: 'add',
};

@Injectable()
export class SectorMoasReceivedAmountStore extends SignalStore<SectorMoasReceivedAmountState> {
  private readonly subsistenceFeesMoAReceivedAmountControllerService = inject(
    SubsistenceFeesMoAReceivedAmountControllerService,
  );

  constructor() {
    super(INITIAL_STATE);
  }

  updateState(state: Partial<SectorMoasReceivedAmountState>) {
    this.setState({ ...this.state, ...state });
  }

  getAndSetReceivedAmount(moaId: number) {
    this.subsistenceFeesMoAReceivedAmountControllerService
      .getSubsistenceFeesMoaReceivedAmountInfo(moaId)
      .pipe(
        catchError((err) => {
          throw new Error(err.message);
        }),
      )
      .subscribe((response) => {
        this.updateState({
          transactionId: response.transactionId,
          businessId: response.businessId,
          name: response.name,
          currentTotalAmount: response.currentTotalAmount,
          receivedAmount: response.receivedAmount,
          receivedAmountHistoryList: response.receivedAmountHistoryList,
        });
      });
  }

  submitReceivedAmount(moaId: number) {
    this.subsistenceFeesMoAReceivedAmountControllerService
      .updateSubsistenceFeesMoaReceivedAmount(moaId, this.state.details)
      .pipe(
        catchError((err) => {
          throw new Error(err);
        }),
      )
      .subscribe(() => {
        this.updateState({ details: this.state.details });
      });
  }
}
