import { Pipe, PipeTransform } from '@angular/core';

import { UserInfoDTO } from 'cca-api';

export function transformUsername(userDto: UserInfoDTO): string {
  return userDto.firstName ? `${userDto.firstName} ${userDto.lastName}` : `${userDto.lastName}`;
}

@Pipe({ name: 'userFullName', pure: true, standalone: true })
export class UserFullNamePipe implements PipeTransform {
  transform = transformUsername;
}
