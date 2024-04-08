package uk.gov.cca.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchByAccountCriteria;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchCriteria;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestSearchCriteriaMapper {
    
    RequestSearchCriteria toRequestSearchCriteria(RequestSearchByAccountCriteria requestSearchByAccountCriteria);
}
