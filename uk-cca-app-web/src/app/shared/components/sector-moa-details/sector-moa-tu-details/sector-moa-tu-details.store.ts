import { inject, Injectable } from '@angular/core';

import { catchError, Observable, pipe, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { SignalStore } from '@netz/common/store';

import {
  SubsistenceFeesMoaFacilityMarkingStatusDTO,
  SubsistenceFeesMoaFacilitySearchResultInfoDTO,
  SubsistenceFeesMoaFacilitySearchResults,
  SubsistenceFeesMoaTargetUnitDetailsDTO,
  SubsistenceFeesMoATargetUnitViewService,
  SubsistenceFeesSearchCriteria,
} from 'cca-api';

export type SectorMoaTUDetailsState = {
  userRoleType: string;
  moaTUDetails: SubsistenceFeesMoaTargetUnitDetailsDTO;
  facilities: SubsistenceFeesMoaFacilitySearchResultInfoDTO[];
  selectedFacilities: Map<string, SubsistenceFeesMoaFacilitySearchResultInfoDTO>;
  totalFacilityItems: number;
};

const INITIAL_STATE: SectorMoaTUDetailsState = {
  userRoleType: '',
  moaTUDetails: null,
  facilities: [],
  selectedFacilities: new Map(),
  totalFacilityItems: 0,
};

@Injectable()
export class SectorMoaTUDetailsStore extends SignalStore<SectorMoaTUDetailsState> {
  private readonly subsistenceFeesMoATargetUnitViewService = inject(SubsistenceFeesMoATargetUnitViewService);
  private readonly userRoleType = inject(AuthStore).select(selectUserRoleType);

  constructor() {
    super(INITIAL_STATE);
  }

  fetchMoaTUDetails(moaTUId: number): Observable<SubsistenceFeesMoaTargetUnitDetailsDTO> {
    return this.subsistenceFeesMoATargetUnitViewService
      .getSubsistenceFeesMoaTargetUnitDetailsById(moaTUId)
      .pipe(this.updateTUMoaDetails());
  }

  updateSelectedTUMoAFacilities(selectedRow: SubsistenceFeesMoaFacilitySearchResultInfoDTO, checked: boolean) {
    if (selectedRow.markFacilitiesStatus === 'CANCELLED') return; // cancelled facilities are not selectable

    const selectedFacilities = this.state.selectedFacilities;

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

  fetchTargetUnitFacilities(
    searchCriteria: SubsistenceFeesSearchCriteria,
  ): Observable<SubsistenceFeesMoaFacilitySearchResults> {
    return this.subsistenceFeesMoATargetUnitViewService.getSubsistenceFeesMoaFacilities(
      this.state.moaTUDetails.moaTargetUnitId,
      searchCriteria,
    );
  }

  updateSelectedMarkedFacilities(
    subsistenceFeesMoaFacilityMarkingStatusDTO: SubsistenceFeesMoaFacilityMarkingStatusDTO,
  ) {
    return this.subsistenceFeesMoATargetUnitViewService
      .markFacilitiesStatusByMoaTargetUnitId(
        this.state.moaTUDetails.moaTargetUnitId,
        subsistenceFeesMoaFacilityMarkingStatusDTO,
      )
      .pipe(
        catchError(() => {
          throw new Error('Failed to mark facilities status');
        }),
      );
  }

  updateTUMoaDetails() {
    return pipe(tap((details) => this.updateState({ moaTUDetails: details })));
  }

  updateFacilities() {
    return pipe(
      tap((results: SubsistenceFeesMoaFacilitySearchResults) =>
        this.updateState({ facilities: results.subsistenceFeesMoaFacilities, totalFacilityItems: results.total }),
      ),
    );
  }

  initialize() {
    this.updateState({
      userRoleType: this.userRoleType(),
    });
  }
}
