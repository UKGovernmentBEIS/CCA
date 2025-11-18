package uk.gov.cca.api.facilityaudit.transform;


import org.mapstruct.Mapper;
import uk.gov.cca.api.facilityaudit.domain.FacilityAudit;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityAuditMapper {

	FacilityAuditViewDTO toFacilityAuditViewDTO(FacilityAudit entity, boolean editable);

	FacilityAuditDTO toFacilityAuditDTO(FacilityAudit entity);
}
