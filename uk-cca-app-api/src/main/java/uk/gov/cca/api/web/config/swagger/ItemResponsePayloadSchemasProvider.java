package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemTargetUnitDTO;

@Component
public class ItemResponsePayloadSchemasProvider extends SwaggerSchemasAbstractProvider {

	@Override
    public void afterPropertiesSet() {
		// this is needed in order to generate swagger implementation for ItemTargetUnitDTO
    	addResolvedShemas(ItemTargetUnitDTO.class.getSimpleName(), ItemTargetUnitDTO.class);
    }
}
