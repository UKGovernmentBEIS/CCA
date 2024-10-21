package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

/**
 * The sector association details DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationDetailsDTO {

    @NotNull(message = "{sectorAssociationDetails.competentAuthority.notNull}")
    private CompetentAuthorityEnum competentAuthority;

    @NotBlank(message = "{sectorAssociationDetails.commonName.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.commonName.size}")
    private String commonName;

    @NotBlank(message = "{sectorAssociationDetails.acronym.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.acronym.size}")
    private String acronym;

    @NotBlank(message = "{sectorAssociationDetails.legalName.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.legalName.size}")
    private String legalName;

    private String facilitatorUserId;

    @NotNull(message = "{sectorAssociationDetails.noticeServiceAddress.notNull}")
    @Valid
    private AddressDTO noticeServiceAddress;

    @NotBlank(message = "{sectorAssociationDetails.energyIntensiveOrEPR.notEmpty}")
    @Size(max = 255, message = "{sectorAssociationDetails.energyIntensiveOrEPR.size}")
    private String energyIntensiveOrEPR;
}
