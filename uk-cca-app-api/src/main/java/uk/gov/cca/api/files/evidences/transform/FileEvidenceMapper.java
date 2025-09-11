package uk.gov.cca.api.files.evidences.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.IOException;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FileEvidenceMapper {

    FileEvidence toFileEvidence(FileDTO fileDTO) throws IOException;

}
