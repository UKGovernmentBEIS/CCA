package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class RequestPayloadSchemasProvider extends SwaggerSchemasAbstractProvider {
    
    @Override
    public void afterPropertiesSet() {
    	//project specific
    }
    
}
