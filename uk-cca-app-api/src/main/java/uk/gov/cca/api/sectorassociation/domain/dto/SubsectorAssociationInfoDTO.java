package uk.gov.cca.api.sectorassociation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsectorAssociationInfoDTO {

    private Long id;
    private String name;
}
