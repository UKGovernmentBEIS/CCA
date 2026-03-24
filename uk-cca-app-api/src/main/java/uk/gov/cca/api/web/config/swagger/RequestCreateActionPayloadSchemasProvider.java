package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;
import uk.gov.netz.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@Component
public class RequestCreateActionPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	// Common
    	addResolvedShemas(ReportRelatedRequestCreateActionPayload.class.getSimpleName(), ReportRelatedRequestCreateActionPayload.class);
    	
    	addResolvedShemas(RequestCreateActionEmptyPayload.class.getSimpleName(), RequestCreateActionEmptyPayload.class);
    	
    	// Target Unit Account Creation
    	addResolvedShemas(TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class.getSimpleName(), TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class);

        // Buy Out Surplus
        addResolvedShemas(BuyOutSurplusRunCreateActionPayload.class.getSimpleName(), BuyOutSurplusRunCreateActionPayload.class);

        // Performance Data Facility Digital Form
        addResolvedShemas(PerformanceDataFacilityDigitalFormRequestCreateActionPayload.class.getSimpleName(), PerformanceDataFacilityDigitalFormRequestCreateActionPayload.class);
    }
    
}
