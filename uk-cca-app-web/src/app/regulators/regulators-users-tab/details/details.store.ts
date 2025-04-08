import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { RegulatorCurrentUserDTO, RegulatorRolePermissionsDTO, RegulatorUserDTO } from 'cca-api';

export type DetailsStoreType = {
  isEditable: boolean;
  isAdd: boolean;
  user?: RegulatorUserDTO | RegulatorCurrentUserDTO;
  userPermissions?: Record<string, 'NONE' | 'EXECUTE' | 'VIEW_ONLY'>;
  regulatorRoles?: RegulatorRolePermissionsDTO[];
  permissionGroupLevels?: Record<string, string[]>;
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
