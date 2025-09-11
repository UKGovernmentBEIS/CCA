import { inject, Injectable } from '@angular/core';

import { catchError, Observable, pipe, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { SignalStore } from '@netz/common/store';

import {
  SubsistenceFeesMoaDetailsDTO,
  SubsistenceFeesMoaFacilityMarkingStatusDTO,
  SubsistenceFeesMoaSearchCriteria,
  SubsistenceFeesMoaTargetUnitSearchResultInfoDTO,
  SubsistenceFeesMoaTargetUnitSearchResults,
  SubsistenceFeesMoAViewService,
} from 'cca-api';

export type SectorMoaDetailsState = {
  userRoleType: string;
  sectorMoaDetails: SubsistenceFeesMoaDetailsDTO;
  targetUnits: SubsistenceFeesMoaTargetUnitSearchResultInfoDTO[];
  selectedTUs: Map<string, SubsistenceFeesMoaTargetUnitSearchResultInfoDTO>;
  totalTUItems: number;
};

const INITIAL_STATE: SectorMoaDetailsState = {
  userRoleType: '',
  sectorMoaDetails: null,
  targetUnits: [],
  selectedTUs: new Map(),
  totalTUItems: 0,
};

@Injectable()
export class SectorMoaDetailsStore extends SignalStore<SectorMoaDetailsState> {
  private readonly subsistenceFeesMoAViewService = inject(SubsistenceFeesMoAViewService);
  private readonly userRoleType = inject(AuthStore).select(selectUserRoleType);

  constructor() {
    super(INITIAL_STATE);
  }

  fetchSectorMoaDetails(moaId: number): Observable<SubsistenceFeesMoaDetailsDTO> {
    return this.subsistenceFeesMoAViewService
      .getSubsistenceFeesMoaDetailsById(moaId)
      .pipe(tap((details) => this.updateState({ sectorMoaDetails: details })));
  }

  updateSelectedTUs(selectedRow: SubsistenceFeesMoaTargetUnitSearchResultInfoDTO, checked: boolean) {
    if (selectedRow.markFacilitiesStatus === 'CANCELLED') return; // cancelled facilities are not selectable

    const selectedTUs = this.state.selectedTUs;

    if (checked) {
      selectedTUs.set(selectedRow.businessId, selectedRow);
    } else {
      selectedTUs.delete(selectedRow.businessId);
    }

    this.updateState({ selectedTUs });
  }

  clearSelectedTUs() {
    this.updateState({ selectedTUs: new Map() });
  }

  fetchTargetUnits(
    searchCriteria: SubsistenceFeesMoaSearchCriteria,
  ): Observable<SubsistenceFeesMoaTargetUnitSearchResults> {
    return this.subsistenceFeesMoAViewService.getSubsistenceFeesMoaTargetUnits(
      this.state.sectorMoaDetails.moaId,
      searchCriteria,
    );
  }

  updateSelectedMarkedFacilities(
    subsistenceFeesMoaFacilityMarkingStatusDTO: SubsistenceFeesMoaFacilityMarkingStatusDTO,
  ) {
    return this.subsistenceFeesMoAViewService
      .markFacilitiesStatusByMoaId(this.state.sectorMoaDetails.moaId, subsistenceFeesMoaFacilityMarkingStatusDTO)
      .pipe(
        catchError(() => {
          throw new Error('Failed to mark facilities status');
        }),
      );
  }

  updateTargetUnits() {
    return pipe(
      tap((results: SubsistenceFeesMoaTargetUnitSearchResults) =>
        this.updateState({ targetUnits: results.subsistenceFeesMoaTargetUnits, totalTUItems: results.total }),
      ),
    );
  }

  initialize() {
    this.updateState({
      userRoleType: this.userRoleType(),
    });
  }
}
