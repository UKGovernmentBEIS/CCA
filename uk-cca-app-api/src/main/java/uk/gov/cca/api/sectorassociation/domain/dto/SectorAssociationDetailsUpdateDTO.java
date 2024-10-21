package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The sector association details update DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationDetailsUpdateDTO {

    @NotBlank(message = "{sectorAssociationDetails.commonName.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.commonName.size}")
    private String commonName;

    @NotBlank(message = "{sectorAssociationDetails.legalName.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.legalName.size}")
    private String legalName;

    @NotNull(message = "{sectorAssociationDetails.noticeServiceAddress.notNull}")
    private AddressDTO noticeServiceAddress;
}