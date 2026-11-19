package uk.gov.cca.api.web.orchestrator.mireport.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.web.orchestrator.mireport.dto.CcaMiReportUserDefinedDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedUpdateDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaMiReportUserDefinedMapper {

    MiReportUserDefinedDTO toMiReportUserDefinedDTO(CcaMiReportUserDefinedDTO ccaMiReportUserDefinedDTO);

    @Mapping(target = "userDefinedDTO", source = "ccaMiReportUserDefinedDTO")
    @Mapping(target = "reasonForChange", constant = "Not provided")
    MiReportUserDefinedUpdateDTO toMiReportUserDefinedUpdateDTO(CcaMiReportUserDefinedDTO ccaMiReportUserDefinedDTO);
}
