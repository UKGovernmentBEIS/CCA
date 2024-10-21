import { Pipe, PipeTransform } from '@angular/core';

import { SectorUserAuthorityDetailsDTO } from 'cca-api';

export enum ContactTypeEnum {
  CONSULTANT = 'Consultant',
  SECTOR_ASSOCIATION = 'Sector association',
  OPERATOR = 'Operator',
}

@Pipe({
  name: 'contactType',
  standalone: true,
})
export class ContactTypePipe implements PipeTransform {
  transform(value: SectorUserAuthorityDetailsDTO['contactType']): string {
    const text = ContactTypeEnum[value];
    if (!text) throw new Error('invalid contact type for sector user');
    return text;
  }
}
