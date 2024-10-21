package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsectorAssociationDTO {

    @NotBlank(message = "{subsectorAssociation.name.notEmpty}")
    @Size(max = 255, message = "{subsectorAssociation.name.size}")
    private String name;
}
