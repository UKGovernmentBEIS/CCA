package uk.gov.cca.api.web.orchestrator.sectorassociation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.user.core.domain.UserBasicInfoDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorAssociationDetailsResponseMapper {

    @Mapping(target = "facilitator", ignore = true)
    SectorAssociationDetailsResponseDTO toSectorAssociationResponseDTO(SectorAssociationDetailsDTO sectorAssociationDetailsDTO, @Context
    UserBasicInfoDTO facilitatorInfo);

    @AfterMapping
    default void addFacilitator(@MappingTarget SectorAssociationDetailsResponseDTO responseDTO, @Context UserBasicInfoDTO facilitatorInfo) {
        if (facilitatorInfo != null) {
            responseDTO.setFacilitator(facilitatorInfo);
        }
    }
}
