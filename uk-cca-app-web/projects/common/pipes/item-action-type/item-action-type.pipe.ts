import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'cca-api';

export const ItemActionEnum: Record<string, string> = {
  TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED: 'Target unit account submitted',
  UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED: 'Underlying agreement application submitted',
  UNDERLYING_AGREEMENT_APPLICATION_REJECTED: 'Underlying agreement application rejected',
  UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED: 'Underlying agreement application accepted',
  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED: 'Underlying agreement application activated',
  UNDERLYING_AGREEMENT_APPLICATION_CANCELLED: 'Underlying agreement application cancelled',
  UNDERLYING_AGREEMENT_APPLICATION_MIGRATED: 'Underlying agreement application migrated',
  ADMIN_TERMINATION_APPLICATION_SUBMITTED: 'Admin termination submitted',
  ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED: 'Admin termination withdrawn submitted',
  ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED: 'Admin termination final decision submitted',
  ADMIN_TERMINATION_APPLICATION_CANCELLED: 'Admin termination cancelled',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED: 'Underlying agreement variation application submitted',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED: 'Underlying agreement variation application rejected',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED: 'Underlying agreement variation application accepted',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED: 'Underlying agreement variation activated',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED: 'Underlying agreement variation application cancelled',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_COMPLETED: 'Underlying agreement variation application completed',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED: 'Underlying agreement variation proposed',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_COMPLETED: 'Underlying agreement variation completed',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_ACTIVATED: 'Underlying agreement variation activated',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_REQUESTED: 'Peer review requested',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_ACCEPTED: 'Peer review agreement submitted',
  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_REJECTED: 'Peer review disagreement submitted',
  PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED: 'Performance report submitted',
  PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED: 'PAT report submitted',
  SUBSISTENCE_FEES_RUN_SUBMITTED: 'Subsistence fees payment request run submitted',
  SUBSISTENCE_FEES_RUN_COMPLETED: 'Subsistence fees payment request run completed',
  SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES: 'Subsistence fees payment request run completed with failures',
  SECTOR_MOA_GENERATED: 'Sector MoA generated',
  TARGET_UNIT_MOA_GENERATED: 'Subsistence fees payment request received',
  BUY_OUT_SURPLUS_RUN_SUBMITTED: 'Buy-out and surplus batch run submitted',
  BUY_OUT_SURPLUS_RUN_COMPLETED: 'Buy-out and surplus batch run completed',
  BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES: 'Buy-out and surplus batch run completed with failures',
  ADMIN_TERMINATION_PEER_REVIEW_REQUESTED: 'Peer review requested',
  ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED: 'Peer review agreement',
  ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_REJECTED: 'Peer review disagreement',
  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_REQUESTED: 'Peer review requested',
  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_ACCEPTED: 'Peer review agreement',
  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_REJECTED: 'Peer review disagreement',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_REQUESTED: 'Peer review requested',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED: 'Peer review agreement',
  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED: 'Peer review disagreement',
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED: 'CCA3 agreement cancelled',
  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED: 'CCA3 agreement activated',
  FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED: 'Pre-audit review completed',
  FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED: 'Audit details and corrective actions completed',
  FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED: 'Track corrective actions completed',
  FACILITY_AUDIT_CANCELLED: 'Audit facility cancelled',
  REQUEST_TERMINATED: 'Workflow terminated by the system',
  CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_UNDERLYING_AGREEMENT_TERMINATED: 'CCA2 Underlying agreement terminated',
  NON_COMPLIANCE_DETAILS_SUBMITTED: 'Non-compliance details provided',
  NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMITTED: 'Notice of intent submitted',
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMITTED: 'Enforcement response notice sent to operator',
  NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED: 'Peer review of notice of intent requested',
  NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED: 'Peer review agreement for notice of intent submitted',
  NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED: 'Peer review disagreement for notice of intent submitted',
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_REQUESTED:
    'Peer review of enforcement response notice requested',
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEWER_ACCEPTED:
    'Peer review agreement for enforcement response notice submitted',
  NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEWER_REJECTED:
    'Peer review disagreement for enforcement response notice submitted',
  NON_COMPLIANCE_CONCLUSION_SUBMITTED: 'Conclusion of non-compliance provided',
  NON_COMPLIANCE_APPEAL_PROVIDED: 'Appeal registration provided',
  NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED: 'Appeal registration provided',
  NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED: 'Appeal outcome provided',
  NON_COMPLIANCE_CLOSED: 'Non-compliance closed',
  NON_COMPLIANCE_CANCELLED: 'Non-compliance details cancelled',
  PERFORMANCE_DATA_FACILITY_SUBMITTED: 'Target period reporting submitted',
  PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CANCELLED: 'Target period reporting cancelled',
  PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED: 'Target period reporting submitted',
  PERFORMANCE_DATA_FACILITY_PROCESSING_SUBMITTED: 'Target period reporting submitted',
  PERFORMANCE_DATA_FACILITY_UPLOAD_CLOSED: 'Target period reporting closed',
};

@Pipe({ name: 'itemActionType', pure: true })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    if (!type) return 'Approved Application';
    return ItemActionEnum[type] || 'Approved Application';
  }
}
