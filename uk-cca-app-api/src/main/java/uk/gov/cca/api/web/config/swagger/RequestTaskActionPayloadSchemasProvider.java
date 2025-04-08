package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
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
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentCancelRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeResponseSubmitRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeSubmitRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;

@Component
public class RequestTaskActionPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	// Common
    	addResolvedShemas(RfiSubmitRequestTaskActionPayload.class.getSimpleName(), RfiSubmitRequestTaskActionPayload.class);
    	addResolvedShemas(RfiResponseSubmitRequestTaskActionPayload.class.getSimpleName(), RfiResponseSubmitRequestTaskActionPayload.class);
    	
    	addResolvedShemas(RdeSubmitRequestTaskActionPayload.class.getSimpleName(), RdeSubmitRequestTaskActionPayload.class);
    	addResolvedShemas(RdeForceDecisionRequestTaskActionPayload.class.getSimpleName(), RdeForceDecisionRequestTaskActionPayload.class);
    	addResolvedShemas(RdeResponseSubmitRequestTaskActionPayload.class.getSimpleName(), RdeResponseSubmitRequestTaskActionPayload.class);
    	
    	addResolvedShemas(PaymentMarkAsReceivedRequestTaskActionPayload.class.getSimpleName(), PaymentMarkAsReceivedRequestTaskActionPayload.class);
    	addResolvedShemas(PaymentCancelRequestTaskActionPayload.class.getSimpleName(), PaymentCancelRequestTaskActionPayload.class);

    	addResolvedShemas(RequestTaskActionEmptyPayload.class.getSimpleName(), RequestTaskActionEmptyPayload.class);

		addResolvedShemas(CcaNotifyOperatorForDecisionRequestTaskActionPayload.class.getSimpleName(), CcaNotifyOperatorForDecisionRequestTaskActionPayload.class);

		// Underlying Agreement
		addResolvedShemas(UnderlyingAgreementSaveRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementSaveRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementSaveReviewRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementSaveReviewRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementActivationSaveRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementActivationSaveRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload.class);

		// Underlying Agreement Variation
		addResolvedShemas(UnderlyingAgreementVariationSaveRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSaveRequestTaskActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload.class);
		addResolvedShemas(UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload.class.getSimpleName(), UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload.class);

		// Admin Termination
        addResolvedShemas(AdminTerminationSaveRequestTaskActionPayload.class.getSimpleName(), AdminTerminationSaveRequestTaskActionPayload.class);
		addResolvedShemas(AdminTerminationFinalDecisionSaveRequestTaskActionPayload.class.getSimpleName(), AdminTerminationFinalDecisionSaveRequestTaskActionPayload.class);
		addResolvedShemas(AdminTerminationWithdrawSaveRequestTaskActionPayload.class.getSimpleName(), AdminTerminationWithdrawSaveRequestTaskActionPayload.class);

		// Performance data download
		addResolvedShemas(PerformanceDataGenerateRequestTaskActionPayload.class.getSimpleName(), PerformanceDataGenerateRequestTaskActionPayload.class);

		// Performance data upload
		addResolvedShemas(PerformanceDataUploadProcessingRequestTaskActionPayload.class.getSimpleName(), PerformanceDataUploadProcessingRequestTaskActionPayload.class);
		
		// PAT
		addResolvedShemas(PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.class.getSimpleName(),
				PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.class);
    }
    
}
