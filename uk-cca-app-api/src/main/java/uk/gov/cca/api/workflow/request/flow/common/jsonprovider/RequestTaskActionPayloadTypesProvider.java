package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.ADMIN_TERMINATION_FINAL_DECISION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.ADMIN_TERMINATION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.ADMIN_TERMINATION_WITHDRAW_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.PERFORMANCE_DATA_DOWNLOAD_GENERATE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.PERFORMANCE_DATA_UPLOAD_PROCESSING_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_ACTIVATION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_APPLICATION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataGenerateRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;


@Component
public class RequestTaskActionPayloadTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                // Common
                new NamedType(CcaNotifyOperatorForDecisionRequestTaskActionPayload.class, NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                // Underlying Agreement
                new NamedType(UnderlyingAgreementSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_APPLICATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD),
                new NamedType(UnderlyingAgreementActivationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_ACTIVATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),
                // Admin Termination
                new NamedType(AdminTerminationSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_SAVE_PAYLOAD),
                new NamedType(AdminTerminationFinalDecisionSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_FINAL_DECISION_SAVE_PAYLOAD),
                new NamedType(AdminTerminationWithdrawSaveRequestTaskActionPayload.class, ADMIN_TERMINATION_WITHDRAW_SAVE_PAYLOAD),
                // Underlying Agreement Variation
                new NamedType(UnderlyingAgreementVariationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_PAYLOAD),
                new NamedType(UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload.class, UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD),

                // Performance data download
                new NamedType(PerformanceDataGenerateRequestTaskActionPayload.class, PERFORMANCE_DATA_DOWNLOAD_GENERATE_PAYLOAD),

                // Performance data upload
                new NamedType(PerformanceDataUploadProcessingRequestTaskActionPayload.class, PERFORMANCE_DATA_UPLOAD_PROCESSING_PAYLOAD),
                
                // PAT
				new NamedType(PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.class,
						CcaRequestTaskActionPayloadType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING_PAYLOAD)
        );
    }

}
