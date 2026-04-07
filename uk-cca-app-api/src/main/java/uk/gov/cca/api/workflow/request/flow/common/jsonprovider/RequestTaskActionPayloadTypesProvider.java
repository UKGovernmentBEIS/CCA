package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataGenerateRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSaveRequestTaskActionPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.*;


@Component
public class RequestTaskActionPayloadTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                // Common
                new NamedType(CcaNotifyOperatorForDecisionRequestTaskActionPayload.class, CCA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                new NamedType(NotifyOperatorForDecisionRequestTaskActionPayload.class, NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                // Underlying Agreement
                new NamedType(UnderlyingAgreementSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_APPLICATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD),
                new NamedType(UnderlyingAgreementActivationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_ACTIVATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                new NamedType(PeerReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_PEER_REVIEW_REQUEST_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD),
                // Admin Termination
                new NamedType(AdminTerminationSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_SAVE_PAYLOAD),
                new NamedType(AdminTerminationFinalDecisionSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_FINAL_DECISION_SAVE_PAYLOAD),
                new NamedType(AdminTerminationWithdrawSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_WITHDRAW_SAVE_PAYLOAD),
                new NamedType(PeerReviewRequestTaskActionPayload.class, ADMIN_TERMINATION_PEER_REVIEW_REQUEST_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionRequestTaskActionPayload.class, ADMIN_TERMINATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD),
                // Underlying Agreement Variation
                new NamedType(UnderlyingAgreementVariationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                new NamedType(PeerReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_REQUEST_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationRegulatorLedSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SAVE_PAYLOAD),

                // Performance data download
                new NamedType(PerformanceDataGenerateRequestTaskActionPayload.class, PERFORMANCE_DATA_DOWNLOAD_GENERATE_PAYLOAD),

                // Performance data upload
                new NamedType(PerformanceDataUploadProcessingRequestTaskActionPayload.class, PERFORMANCE_DATA_UPLOAD_PROCESSING_PAYLOAD),

                // PAT
                new NamedType(PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.class,
                        CcaRequestTaskActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING_PAYLOAD),

                // CCA3 Existing Facilities Migration
                new NamedType(Cca3ExistingFacilitiesMigrationAccountProcessingActivationSaveRequestTaskActionPayload.class,
                        CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_PAYLOAD),

                // Facility Audit
                new NamedType(PreAuditReviewSubmitSaveRequestTaskActionPayload.class, FACILITY_AUDIT_PRE_AUDIT_REVIEW_SAVE_PAYLOAD),
                new NamedType(AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload.class, FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SAVE_PAYLOAD),
                new NamedType(AuditTrackCorrectiveActionsSaveRequestTaskActionPayload.class, FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_PAYLOAD),
                new NamedType(AuditTrackCorrectiveActionsSubmitRequestTaskActionPayload.class, FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD),

                // Non Compliance
                new NamedType(PeerReviewRequestTaskActionPayload.class, NON_COMPLIANCE_PEER_REVIEW_REQUEST_PAYLOAD),
                new NamedType(NonComplianceDetailsSubmitSaveRequestTaskActionPayload.class, NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD),
                new NamedType(NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload.class, NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_PAYLOAD),
                new NamedType(NonComplianceCloseRequestTaskActionPayload.class, NON_COMPLIANCE_CLOSE_TASK_PAYLOAD));
    }

}
