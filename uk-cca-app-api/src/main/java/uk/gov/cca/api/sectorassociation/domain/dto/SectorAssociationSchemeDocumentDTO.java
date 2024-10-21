package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectorAssociationSchemeDocumentDTO {

    @NotBlank(message = "{sectorAssociationSchemeDocument.uuid.notEmpty}")
    private String uuid;

    @NotNull(message = "{sectorAssociationSchemeDocument.id.notEmpty}")
    private long id;

    @NotBlank(message = "{sectorAssociationSchemeDocument.fileName.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationSchemeDocument.fileName.size}")
    private String fileName;

    @Positive(message = "{sectorAssociationSchemeDocument.fileSize.positive}")
    private long fileSize;

    @NotBlank(message = "{sectorAssociationSchemeDocument.fileType.notEmpty}")
    private String fileType;
}
