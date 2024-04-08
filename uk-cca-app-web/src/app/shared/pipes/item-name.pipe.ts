import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemName', pure: true, standalone: true })
export class ItemNamePipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  transform(value: ItemDTO['taskType'], year?: string | number): string {
    switch (value) {
      default:
        return null;
    }
  }
}
