package uk.gov.cca.api.web.config.swagger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;

abstract class SwaggerSchemasAbstractProvider implements SwaggerSchemasProvider, InitializingBean {

	protected final Map<String, Schema> schemas = new HashMap<>();
	
	@Override
    public Map<String, Schema> getSchemas() {
        return schemas;
    }
	
	protected void addResolvedShemas(String metadataKey, Type metadataClass) {
    	ResolvedSchema metadata = ModelConverters.getInstance().readAllAsResolvedSchema(metadataClass);
        schemas.put(metadataKey, metadata.schema);
        schemas.putAll(metadata.referencedSchemas);
    }
}
