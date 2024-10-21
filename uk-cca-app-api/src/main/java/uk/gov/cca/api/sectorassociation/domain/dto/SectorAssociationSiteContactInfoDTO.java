package uk.gov.cca.api.sectorassociation.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectorAssociationSiteContactInfoDTO {

    private Long sectorAssociationId;

    private String sectorName;

    private String userId;
}
