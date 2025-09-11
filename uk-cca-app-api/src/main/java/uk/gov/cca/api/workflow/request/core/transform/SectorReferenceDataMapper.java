package uk.gov.cca.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationMeasurementInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorReferenceDataMapper {

    SectorAssociationDetails toSectorAssociationDetails(SectorAssociationMeasurementInfoDTO sectorAssociationMeasurementInfoDTO);

    SectorAssociationInfo toSectorAssociationInfo(SectorAssociationInfoNameDTO sectorAssociationInfoNameDTO);
}
