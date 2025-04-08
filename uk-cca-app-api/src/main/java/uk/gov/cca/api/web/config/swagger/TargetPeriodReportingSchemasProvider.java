package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class TargetPeriodReportingSchemasProvider extends SwaggerSchemasAbstractProvider {

    @Override
    public void afterPropertiesSet() {
        addResolvedShemas(TP6PerformanceData.class.getSimpleName(), TP6PerformanceData.class);
    }
}