package uk.gov.cca.api.sectorassociation.domain.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class SubsectorAssociationSchemesDTO {

	@NotBlank(message = "{subsectorAssociation.name.notEmpty}")
    @Size(max = 255, message = "{subsectorAssociation.name.size}")
    private String name;
	
	@NotEmpty
	private Map<SchemeVersion, @Valid SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap;
}
