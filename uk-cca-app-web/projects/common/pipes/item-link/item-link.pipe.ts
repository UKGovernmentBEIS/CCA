import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemLink', pure: true, standalone: true })
export class ItemLinkPipe implements PipeTransform {
  transform(value: ItemDTO, path: string = '/'): any[] {
    return this.transformWorkflowUrl(value, path);
  }

  private transformWorkflowUrl(value: ItemDTO, routerLooks: string) {
    switch (value?.requestType) {
      case 'UNDERLYING_AGREEMENT':
      case 'ADMIN_TERMINATION':
      case 'UNDERLYING_AGREEMENT_VARIATION':
      case 'PERFORMANCE_DATA_DOWNLOAD':
      case 'PERFORMANCE_DATA_UPLOAD':
      case 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD':
      case 'ADMIN_TERMINATION_APPLICATION_PEER_REVIEW':
      case 'ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW':
      case 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW':
      case 'UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW':
        return [routerLooks + 'tasks', value.taskId];

      default:
        return ['.'];
    }
  }
}
