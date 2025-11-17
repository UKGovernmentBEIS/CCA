import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemType' })
export class ItemTypePipe implements PipeTransform {
  transform(value: ItemDTO['requestType']): string {
    switch (value) {
      default:
        return null;
    }
  }
}
