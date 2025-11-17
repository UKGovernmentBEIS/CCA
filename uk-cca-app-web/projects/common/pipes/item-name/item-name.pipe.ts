import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'cca-api';

@Pipe({ name: 'itemName', pure: true })
export class ItemNamePipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  transform(value: ItemDTO['taskType'], year?: string | number): string {
    switch (value) {
      case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT':
        return 'Apply for underlying agreement';
      case 'UNDERLYING_AGREEMENT_APPLICATION_REVIEW':
        return 'Review application for underlying agreement';
      case 'UNDERLYING_AGREEMENT_WAIT_FOR_REVIEW':
        return 'Application for underlying agreement sent for review';
      case 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION':
        return 'Upload target unit assent';
      case 'UNDERLYING_AGREEMENT_WAIT_FOR_ACTIVATION':
        return `Application for underlying agreement awaiting operator's assent/activation`;
      case 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW':
        return 'Peer review application for underlying agreement';
      case 'UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW':
        return 'Application for underlying agreement sent to peer reviewer';

      case 'ADMIN_TERMINATION_APPLICATION_SUBMIT':
        return 'Admin termination';
      case 'ADMIN_TERMINATION_APPLICATION_WITHDRAW':
        return 'Withdraw admin termination';
      case 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION':
        return 'Admin termination final decision';
      case 'ADMIN_TERMINATION_APPLICATION_PEER_REVIEW':
        return 'Peer review admin termination request';
      case 'ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW':
        return 'Admin termination sent for peer review';

      case 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT':
        return 'Apply to vary the underlying agreement';
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW':
        return 'Review underlying agreement variation';
      case 'UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_REVIEW':
        return 'Application for underlying agreement variation sent for review';
      case 'UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_ACTIVATION':
        return 'Application to vary underlying agreement sent for review';
      case 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION':
        return 'Upload target unit assent on variation';
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW':
        return 'Peer review application for underlying agreement variation';
      case 'UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW':
        return 'Application for underlying agreement variation sent to peer reviewer';

      case 'PERFORMANCE_DATA_DOWNLOAD_SUBMIT':
        return 'Download target period reporting (TPR) spreadsheets';
      case 'PERFORMANCE_DATA_UPLOAD_SUBMIT':
        return 'Target period reporting (TPR) spreadsheets upload';
      case 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT':
        return 'Performance account template (PAT) upload';

      case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION':
        return 'Upload target unit assent';

      case 'PRE_AUDIT_REVIEW_SUBMIT':
        return 'Pre-audit review';

      default:
        return null;
    }
  }
}
