package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeResponseRequestTaskPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

@Component
public class RequestTaskPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {

    @Override
    public void afterPropertiesSet() {
    	// Common
    	addResolvedShemas(RfiResponseSubmitRequestTaskPayload.class.getSimpleName(), RfiResponseSubmitRequestTaskPayload.class);

    	addResolvedShemas(RdeForceDecisionRequestTaskPayload.class.getSimpleName(), RdeForceDecisionRequestTaskPayload.class);
    	addResolvedShemas(RdeResponseRequestTaskPayload.class.getSimpleName(), RdeResponseRequestTaskPayload.class);

    	addResolvedShemas(PaymentMakeRequestTaskPayload.class.getSimpleName(), PaymentMakeRequestTaskPayload.class);
    	addResolvedShemas(PaymentTrackRequestTaskPayload.class.getSimpleName(), PaymentTrackRequestTaskPayload.class);
    	addResolvedShemas(PaymentConfirmRequestTaskPayload.class.getSimpleName(), PaymentConfirmRequestTaskPayload.class);

    	// Underlying Agreement
		addResolvedShemas(UnderlyingAgreementSubmitRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementSubmitRequestTaskPayload.class);
		addResolvedShemas(UnderlyingAgreementReviewRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementReviewRequestTaskPayload.class);
		addResolvedShemas(UnderlyingAgreementActivationRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementActivationRequestTaskPayload.class);

		// Underlying Agreement Variation
		addResolvedShemas(UnderlyingAgreementVariationSubmitRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementVariationSubmitRequestTaskPayload.class);
		addResolvedShemas(UnderlyingAgreementVariationReviewRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementVariationReviewRequestTaskPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationActivationRequestTaskPayload.class.getSimpleName(), UnderlyingAgreementVariationActivationRequestTaskPayload.class);

        // Admin Termination
        addResolvedShemas(AdminTerminationSubmitRequestTaskPayload.class.getSimpleName(), AdminTerminationSubmitRequestTaskPayload.class);
		addResolvedShemas(AdminTerminationFinalDecisionRequestTaskPayload.class.getSimpleName(), AdminTerminationFinalDecisionRequestTaskPayload.class);
		addResolvedShemas(AdminTerminationWithdrawRequestTaskPayload.class.getSimpleName(), AdminTerminationWithdrawRequestTaskPayload.class);

		// Performance data download
		addResolvedShemas(PerformanceDataDownloadSubmitRequestTaskPayload.class.getSimpleName(), PerformanceDataDownloadSubmitRequestTaskPayload.class);

		// Performance data upload
		addResolvedShemas(PerformanceDataUploadSubmitRequestTaskPayload.class.getSimpleName(), PerformanceDataUploadSubmitRequestTaskPayload.class);
		
		// PAT
		addResolvedShemas(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.class.getSimpleName(),
				PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.class);
    }

}
