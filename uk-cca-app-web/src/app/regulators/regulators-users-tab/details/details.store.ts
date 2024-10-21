import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { RegulatorRolePermissionsDTO, RegulatorUserDTO } from 'cca-api';

export type DetailsStoreType = {
  isEditable: boolean;
  isAdd: boolean;
  user?: RegulatorUserDTO;
  userPermissions?: { [key: string]: 'NONE' | 'EXECUTE' | 'VIEW_ONLY' };
  regulatorRoles?: RegulatorRolePermissionsDTO[];
  permissionGroupLevels?: { [key: string]: string[] };
};

const initialState: DetailsStoreType = {
  isEditable: false,
  isAdd: false,
};

@Injectable()
export class DetailsStore extends SignalStore<DetailsStoreType> {
  constructor() {
    super(initialState);
  }

  update(state: Partial<DetailsStoreType>) {
    this.setState({ ...this.state, ...state });
  }
}
