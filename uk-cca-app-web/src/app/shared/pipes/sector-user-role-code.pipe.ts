import { Pipe, PipeTransform } from '@angular/core';

import { SectorInvitedUserInfoDTO } from 'cca-api';

export enum SectorUserRoleCode {
  sector_user_administrator = 'Administrator user',
  sector_user_basic_user = 'Basic user',
}

@Pipe({ name: 'sectorUserRoleCode' })
export class SectorUserRoleCodePipe implements PipeTransform {
  transform(value: SectorInvitedUserInfoDTO['roleCode']): string {
    const type = SectorUserRoleCode[value];
    if (!type) throw new Error('invalid role code for sector user');
    return type;
  }
}
