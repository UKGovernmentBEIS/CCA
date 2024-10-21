import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { SectorAssociationContactDTO, SectorAssociationDetailsUpdateDTO, SectorAssociationResponseDTO } from 'cca-api';

const initialState: SectorAssociationResponseDTO = {
  sectorAssociationContact: null,
  sectorAssociationDetails: null,
};

@Injectable()
export class ActiveSectorStore extends SignalStore<SectorAssociationResponseDTO> {
  constructor() {
    super(initialState);
  }
  updateDetails(dto: SectorAssociationDetailsUpdateDTO): void {
    this.updateState({ sectorAssociationDetails: { ...this.state.sectorAssociationDetails, ...dto } });
  }
  updateContactDetails(dto: SectorAssociationContactDTO): void {
    this.updateState({ sectorAssociationContact: { ...this.state.sectorAssociationContact, ...dto } });
  }
}
