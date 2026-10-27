import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

import { getItemName } from './item-name.util';

@Pipe({ name: 'itemName', pure: true })
export class ItemNamePipe implements PipeTransform {
  transform(value: ItemDTO['taskType']): string | null {
    return getItemName(value);
  }
}
