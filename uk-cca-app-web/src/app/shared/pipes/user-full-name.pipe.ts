import { Pipe, PipeTransform } from '@angular/core';

import { UserInfoDTO } from 'cca-api';

@Pipe({ name: 'userFullName', pure: true, standalone: true })
export class UserFullNamePipe implements PipeTransform {
  transform(userDto: UserInfoDTO): string {
    return userDto.firstName ? `${userDto.firstName} ${userDto.lastName}` : `${userDto.lastName}`;
  }
}
