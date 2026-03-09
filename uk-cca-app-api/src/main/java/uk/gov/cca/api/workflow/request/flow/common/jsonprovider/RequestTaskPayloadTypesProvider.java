package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain.AdminTerminationPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.domain.UnderlyingAgreementPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.domain.UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.domain.UnderlyingAgreementVariationPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import java.util.List;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.ADMIN_TERMINATION_WITHDRAW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.PERFORMANCE_DATA_UPLOAD_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD;

@Component
public class RequestTaskPayloadTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                // Underlying Agreement
                new NamedType(UnderlyingAgreementSubmitRequestTaskPayload.class, UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD),
                new NamedType(UnderlyingAgreementReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementActivationRequestTaskPayload.class, UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION_PAYLOAD),
                new NamedType(UnderlyingAgreementPeerReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD),

                // Admin Termination
                new NamedType(AdminTerminationSubmitRequestTaskPayload.class, ADMIN_TERMINATION_SUBMIT_PAYLOAD),
                new NamedType(AdminTerminationFinalDecisionRequestTaskPayload.class, ADMIN_TERMINATION_FINAL_DECISION_PAYLOAD),
                new NamedType(AdminTerminationWithdrawRequestTaskPayload.class, ADMIN_TERMINATION_WITHDRAW_PAYLOAD),
                new NamedType(AdminTerminationPeerReviewRequestTaskPayload.class, ADMIN_TERMINATION_PEER_REVIEW_PAYLOAD),
                new NamedType(AdminTerminationSubmitRequestTaskPayload.class, ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_PAYLOAD),

                // Underlying Agreement Variation
                new NamedType(UnderlyingAgreementVariationSubmitRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationActivationRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationPeerReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload.class, UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_PAYLOAD),

                // Performance data download
                new NamedType(PerformanceDataDownloadSubmitRequestTaskPayload.class, PERFORMANCE_DATA_DOWNLOAD_SUBMIT_PAYLOAD),

                // Performance data upload
                new NamedType(PerformanceDataUploadSubmitRequestTaskPayload.class, PERFORMANCE_DATA_UPLOAD_SUBMIT_PAYLOAD),

                // PAT
                new NamedType(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.class, CcaRequestTaskPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD),

                // CCA3 Existing Facilities Migration
                new NamedType(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.class, CcaRequestTaskPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD),

                // Facility Audit
                new NamedType(PreAuditReviewSubmitRequestTaskPayload.class, PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD),
                new NamedType(AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.class, AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD),
                new NamedType(AuditTrackCorrectiveActionsRequestTaskPayload.class, AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD),

                // Nom Compliance
                new NamedType(NonComplianceDetailsSubmitRequestTaskPayload.class, NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD));
    }

}
