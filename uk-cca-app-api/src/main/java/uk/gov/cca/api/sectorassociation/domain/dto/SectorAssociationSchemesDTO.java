package uk.gov.cca.api.sectorassociation.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class SectorAssociationSchemesDTO {

    @NotEmpty
    private Map<SchemeVersion, @Valid SectorAssociationSchemeDTO> sectorAssociationSchemeMap;

    private List<@Valid SubsectorAssociationInfoDTO> subsectorAssociations;
}
