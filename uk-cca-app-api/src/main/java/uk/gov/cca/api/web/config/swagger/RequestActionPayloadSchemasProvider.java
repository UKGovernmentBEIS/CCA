package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

@Component
public class RequestActionPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	//common
    	addResolvedShemas(RfiResponseSubmittedRequestActionPayload.class.getSimpleName(), RfiResponseSubmittedRequestActionPayload.class);
    	addResolvedShemas(RfiSubmittedRequestActionPayload.class.getSimpleName(), RfiSubmittedRequestActionPayload.class);
    	
    	addResolvedShemas(RdeDecisionForcedRequestActionPayload.class.getSimpleName(), RdeDecisionForcedRequestActionPayload.class);
    	addResolvedShemas(RdeRejectedRequestActionPayload.class.getSimpleName(), RdeRejectedRequestActionPayload.class);
    	addResolvedShemas(RdeSubmittedRequestActionPayload.class.getSimpleName(), RdeSubmittedRequestActionPayload.class);
    	
    	addResolvedShemas(PaymentProcessedRequestActionPayload.class.getSimpleName(), PaymentProcessedRequestActionPayload.class);
    	addResolvedShemas(PaymentCancelledRequestActionPayload.class.getSimpleName(), PaymentCancelledRequestActionPayload.class);
    	
    	//project specific
    	
    	addResolvedShemas(TargetUnitAccountCreationSubmittedRequestActionPayload.class.getSimpleName(), TargetUnitAccountCreationSubmittedRequestActionPayload.class);

        // Underlying Agreement
    	addResolvedShemas(UnderlyingAgreementSubmittedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementSubmittedRequestActionPayload.class);
    	addResolvedShemas(UnderlyingAgreementRejectedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementRejectedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementAcceptedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementAcceptedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementActivatedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementActivatedRequestActionPayload.class);

        // Admin Termination
    	addResolvedShemas(AdminTerminationSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationSubmittedRequestActionPayload.class);
    	addResolvedShemas(AdminTerminationFinalDecisionSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationFinalDecisionSubmittedRequestActionPayload.class);
    	addResolvedShemas(AdminTerminationWithdrawSubmittedRequestActionPayload.class.getSimpleName(), AdminTerminationWithdrawSubmittedRequestActionPayload.class);

        // Underlying Agreement Variation
        addResolvedShemas(UnderlyingAgreementVariationSubmittedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationSubmittedRequestActionPayload.class);
        addResolvedShemas(UnderlyingAgreementVariationAcceptedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationAcceptedRequestActionPayload.class);
		addResolvedShemas(UnderlyingAgreementVariationRejectedRequestActionPayload.class.getSimpleName(), UnderlyingAgreementVariationRejectedRequestActionPayload.class);
    }
    
}
