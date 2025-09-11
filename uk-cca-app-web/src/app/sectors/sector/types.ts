import { GovukSelectOption } from '@netz/govuk-components';

import {
  SectorAssociationResponseDTO,
  SectorUserAuthorityDetailsDTO,
  TargetUnitAccountDetailsResponseDTO,
} from 'cca-api';

export const RoleCodes = ['sector_user_administrator', 'sector_user_basic_user'] as const;

export type RoleCode = (typeof RoleCodes)[number];

export const roleOptions: GovukSelectOption<RoleCode>[] = [
  { text: 'Administrator user', value: 'sector_user_administrator' },
  { text: 'Basic user', value: 'sector_user_basic_user' },
];

export const validateRoleCode = (a: unknown): a is RoleCode =>
  typeof a === 'string' && (a === 'sector_user_administrator' || a === 'sector_user_basic_user');

export const isAdmin = (a: RoleCode) => a === 'sector_user_administrator';

export const ContactTypeOptions = ['SECTOR_ASSOCIATION', 'CONSULTANT'] as const;
export type ContactType = (typeof ContactTypeOptions)[number];

export const OperatorContactTypeOptions = ['OPERATOR', 'CONSULTANT'] as const;
export type OperatorContactType = (typeof OperatorContactTypeOptions)[number];

export type SectorRouteData = {
  details: {
    sectorAssociationInfo: SectorAssociationResponseDTO;
  };
  targetUnit?: TargetUnitAccountDetailsResponseDTO;
  sectorUserDetails: SectorUserAuthorityDetailsDTO;
  canEditSectorUser: boolean;
  isEditable: boolean;
};
