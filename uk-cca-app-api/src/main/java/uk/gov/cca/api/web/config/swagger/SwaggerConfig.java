package uk.gov.cca.api.web.config.swagger;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.type.SimpleType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Year;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuration for REST API documentation.
 */
@Configuration
public class SwaggerConfig implements InitializingBean {
    private final BuildProperties buildProperties;

    public SwaggerConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    	applyCustomSchemaConversions();   
	}

    @Bean
    public OpenAPI customOpenAPI(List<SwaggerSchemasProvider> swaggerSchemasProviders) {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI().info(new Info()
                        .title("API Documentation")
                        .version(String.format("%s %s", buildProperties.getName(), buildProperties.getVersion()))
                        .description("API Documentation"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                		.schemas(swaggerSchemasProviders.stream()
                                .map(SwaggerSchemasProvider::getSchemas)
                                .flatMap(schemas -> schemas.entrySet().stream())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (scheme1, scheme2) -> scheme1)))
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")));
    }
    
    private void applyCustomSchemaConversions() {
        ModelConverters.getInstance().addConverter(new YearConverter());
        ModelConverters.getInstance().addConverter(new BigDecimalConverter());
    }
    
    private static class YearConverter implements ModelConverter {
        @Override
        public Schema<?> resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> chain) {
			Type type = annotatedType.getType();
			if (type instanceof SimpleType) {
				SimpleType simpleType = (SimpleType) type;

				if (Year.class.isAssignableFrom(simpleType.getRawClass())) {
					Schema<Year> schema = new Schema<>();
					schema.setType("integer");
					schema.setFormat("int16");
					return schema;
				}
			}

			// It's needed to follow chain for unresolved types
			if (chain.hasNext()) {
				return chain.next().resolve(annotatedType, context, chain);
			}
			return null;
        }
    }
    
    private static class BigDecimalConverter implements ModelConverter {
        @Override
        public Schema<?> resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> chain) {
			Type type = annotatedType.getType();
			if (type instanceof SimpleType) {
				SimpleType simpleType = (SimpleType) type;

				if (BigDecimal.class.isAssignableFrom(simpleType.getRawClass())) {
					Schema<BigDecimal> schema = new Schema<>();
					schema.setType("string");
					schema.setFormat("decimal");
					return schema;
				}
			}

			if (chain.hasNext()) {
				return chain.next().resolve(annotatedType, context, chain);
			}
			return null;
        }
    }

	
}