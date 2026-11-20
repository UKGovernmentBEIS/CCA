package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface EmissionsTradingSchemeDataMapper {

    EmissionsTradingSchemeDataReportInfoDTO toEmissionsTradingSchemeDataReportInfoDTO(EmissionsTradingSchemeDataEntity entity);

    EmissionsTradingSchemeDataReportDetailsDTO toEmissionsTradingSchemeDataReportDetailsDTO(EmissionsTradingSchemeDataEntity entity);
}
