import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemLink', pure: true })
export class ItemLinkPipe implements PipeTransform {
  transform(value: ItemDTO, path = '/'): (string | number | undefined)[] {
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
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW':
      case 'UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW':
      case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING':
      case 'FACILITY_AUDIT':
      case 'NON_COMPLIANCE':
      case 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW':
      case 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_WAIT_FOR_PEER_REVIEW':
      case 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM':
      case 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD':
        return [routerLooks + 'tasks', value.taskId];

      default:
        return ['.'];
    }
  }
}
