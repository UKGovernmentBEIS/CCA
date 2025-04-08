package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.netz.api.mireport.customreport.CustomMiReportParams;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class MiReportParamsSchemasProvider extends SwaggerSchemasAbstractProvider {

	@Override
    public void afterPropertiesSet() {
    	addResolvedShemas(CustomMiReportParams.class.getSimpleName(), CustomMiReportParams.class);
    }
}
