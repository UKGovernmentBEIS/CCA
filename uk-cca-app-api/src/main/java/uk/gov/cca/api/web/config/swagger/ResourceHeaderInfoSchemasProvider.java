package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityHeaderInfoDTO;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class ResourceHeaderInfoSchemasProvider extends SwaggerSchemasAbstractProvider {

    @Override
    public void afterPropertiesSet() throws Exception {
        addResolvedShemas(TargetUnitAccountHeaderInfoDTO.class.getSimpleName(), TargetUnitAccountHeaderInfoDTO.class);
        addResolvedShemas(FacilityHeaderInfoDTO.class.getSimpleName(), FacilityHeaderInfoDTO.class);
    }
}
