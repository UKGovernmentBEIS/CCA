package uk.gov.cca.api.targetperiodreporting.targetperiod.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CertificationPeriodMapper {

    @Mapping(target = "targetPeriodType", source = "targetPeriod.businessId")
    @Mapping(target = "certificationPeriodType", source = "businessId")
    CertificationPeriodDTO toCertificationPeriodDTO(CertificationPeriod certificationPeriod);

    CertificationPeriodInfoDTO toCertificationPeriodInfoDTO(CertificationPeriod certificationPeriod);
}
