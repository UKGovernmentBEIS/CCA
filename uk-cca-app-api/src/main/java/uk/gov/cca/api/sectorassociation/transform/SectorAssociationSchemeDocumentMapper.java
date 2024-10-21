package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorAssociationSchemeDocumentMapper {

    FileDTO sectorAssociationSchemeDocumentToFileDTO(SectorAssociationSchemeDocument sectorAssociationSchemeDocument);
}
