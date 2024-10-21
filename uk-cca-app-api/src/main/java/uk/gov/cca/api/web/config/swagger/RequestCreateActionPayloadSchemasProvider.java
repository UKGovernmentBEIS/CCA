package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@Component
public class RequestCreateActionPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	//common
    	addResolvedShemas(ReportRelatedRequestCreateActionPayload.class.getSimpleName(), ReportRelatedRequestCreateActionPayload.class);
    	
    	addResolvedShemas(RequestCreateActionEmptyPayload.class.getSimpleName(), RequestCreateActionEmptyPayload.class);
    	
    	//project specific
    	addResolvedShemas(TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class.getSimpleName(), TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class);
    }
    
}
