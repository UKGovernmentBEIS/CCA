package uk.gov.cca.api.web.config.swagger;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

public interface SwaggerSchemasProvider {
	
    Map<String, Schema> getSchemas();
    
}
