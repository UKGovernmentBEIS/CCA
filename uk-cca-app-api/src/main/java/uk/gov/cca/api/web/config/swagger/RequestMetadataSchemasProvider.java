package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class RequestMetadataSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
        // Performance data upload
        addResolvedShemas(PerformanceDataSpreadsheetProcessingRequestMetadata.class.getSimpleName(), PerformanceDataSpreadsheetProcessingRequestMetadata.class);
        // Subsistence fees
        addResolvedShemas(SubsistenceFeesRunRequestMetadata.class.getSimpleName(), SubsistenceFeesRunRequestMetadata.class);
        // Buy Out Surplus
        addResolvedShemas(BuyOutSurplusRunRequestMetadata.class.getSimpleName(), BuyOutSurplusRunRequestMetadata.class);
    }
    
}
