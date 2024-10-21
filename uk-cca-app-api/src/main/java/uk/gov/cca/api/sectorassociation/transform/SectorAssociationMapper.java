package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses = {LocationMapper.class,
    SectorAssociationContactMapper.class})
public interface SectorAssociationMapper {

    @Mapping(source = "name", target = "sectorAssociationDetails.commonName")
    @Mapping(source = "acronym", target = "sectorAssociationDetails.acronym")
    @Mapping(source = "legalName", target = "sectorAssociationDetails.legalName")
    @Mapping(source = "energyEprFactor", target = "sectorAssociationDetails.energyIntensiveOrEPR")
    @Mapping(source = "location", target = "sectorAssociationDetails.noticeServiceAddress")
    @Mapping(source = "competentAuthority", target = "sectorAssociationDetails.competentAuthority")
    @Mapping(source = "facilitatorUserId", target = "sectorAssociationDetails.facilitatorUserId")
    @Mapping(source = "sectorAssociationContact", target = "sectorAssociationContact")
    SectorAssociationDTO toSectorAssociationDTO(SectorAssociation sectorAssociation);

    @Mapping(source = "sectorAssociationDetails.commonName", target = "name")
    @Mapping(source = "sectorAssociationDetails.acronym", target = "acronym")
    @Mapping(source = "sectorAssociationDetails.legalName", target = "legalName")
    @Mapping(source = "sectorAssociationDetails.energyIntensiveOrEPR", target = "energyEprFactor")
    @Mapping(source = "sectorAssociationDetails.noticeServiceAddress", target = "location")
    @Mapping(source = "sectorAssociationDetails.competentAuthority", target = "competentAuthority")
    @Mapping(source = "sectorAssociationDetails.facilitatorUserId", target = "facilitatorUserId")
    @Mapping(source = "sectorAssociationContact", target = "sectorAssociationContact")
    SectorAssociation toSectorAssociation(SectorAssociationDTO sectorAssociationDTO);
}