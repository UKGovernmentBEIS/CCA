import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemLink', pure: true, standalone: true })
export class ItemLinkPipe implements PipeTransform {
  transform(value: ItemDTO): any[] {
    return this.transformWorkflowUrl(value);
  }

  private transformWorkflowUrl(value: ItemDTO) {
    switch (value?.requestType) {
      default:
        return ['.'];
    }
  }
}
