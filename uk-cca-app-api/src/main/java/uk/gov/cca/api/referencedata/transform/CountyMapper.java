package uk.gov.cca.api.referencedata.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.referencedata.domain.County;
import uk.gov.cca.api.referencedata.domain.dto.CountyDTO;

/**
 * The county mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CountyMapper extends ReferenceDataMapper<County, CountyDTO> {

}
