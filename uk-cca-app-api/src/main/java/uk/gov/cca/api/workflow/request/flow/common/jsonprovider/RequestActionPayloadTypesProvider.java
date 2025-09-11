package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.migration.underlyingagreement.request.UnderlyingAgreementMigratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import java.util.List;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.ADMIN_TERMINATION_FINAL_DECISION_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.ADMIN_TERMINATION_PEER_REVIEW_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.ADMIN_TERMINATION_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.ADMIN_TERMINATION_WITHDRAW_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.SECTOR_MOA_GENERATED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TARGET_UNIT_MOA_GENERATED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACCEPTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_MIGRATED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_PEER_REVIEW_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_REJECTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD;


@Component
public class RequestActionPayloadTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                // Target Unit Account
                new NamedType(TargetUnitAccountCreationSubmittedRequestActionPayload.class, TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD),

                // Underlying Agreement
                new NamedType(UnderlyingAgreementSubmittedRequestActionPayload.class, UNDERLYING_AGREEMENT_SUBMITTED_PAYLOAD),
                new NamedType(UnderlyingAgreementRejectedRequestActionPayload.class, UNDERLYING_AGREEMENT_REJECTED_PAYLOAD),
                new NamedType(UnderlyingAgreementAcceptedRequestActionPayload.class, UNDERLYING_AGREEMENT_ACCEPTED_PAYLOAD),
                new NamedType(UnderlyingAgreementActivatedRequestActionPayload.class, UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD),

                new NamedType(UnderlyingAgreementMigratedRequestActionPayload.class, UNDERLYING_AGREEMENT_MIGRATED_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionSubmittedRequestActionPayload.class, UNDERLYING_AGREEMENT_PEER_REVIEW_SUBMITTED_PAYLOAD),

                // Admin Termination
                new NamedType(AdminTerminationSubmittedRequestActionPayload.class, ADMIN_TERMINATION_SUBMITTED_PAYLOAD),
                new NamedType(AdminTerminationFinalDecisionSubmittedRequestActionPayload.class, ADMIN_TERMINATION_FINAL_DECISION_SUBMITTED_PAYLOAD),
                new NamedType(AdminTerminationWithdrawSubmittedRequestActionPayload.class, ADMIN_TERMINATION_WITHDRAW_SUBMITTED_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionSubmittedRequestActionPayload.class, ADMIN_TERMINATION_PEER_REVIEW_SUBMITTED_PAYLOAD),

                // Underlying Agreement Variation
                new NamedType(UnderlyingAgreementVariationSubmittedRequestActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationAcceptedRequestActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationRejectedRequestActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationActivatedRequestActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD),
                new NamedType(CcaPeerReviewDecisionSubmittedRequestActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_SUBMITTED_PAYLOAD),

                // Performance data upload
                new NamedType(PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload.class, PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED_PAYLOAD),

                // PAT
                new NamedType(PerformanceAccountTemplateProcessingSubmittedRequestActionPayload.class, CcaRequestActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD),

                // Subsistence fees
                new NamedType(SubsistenceFeesRunCompletedRequestActionPayload.class, SUBSISTENCE_FEES_RUN_COMPLETED_PAYLOAD),
                new NamedType(SectorMoaGeneratedRequestActionPayload.class, SECTOR_MOA_GENERATED_PAYLOAD),
                new NamedType(TargetUnitMoaGeneratedRequestActionPayload.class, TARGET_UNIT_MOA_GENERATED_PAYLOAD),

                // Buy Out Surplus
                new NamedType(BuyOutSurplusRunCompletedRequestActionPayload.class, BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD),
                new NamedType(TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload.class, TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD),
                new NamedType(TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload.class, TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
        );
    }

}
