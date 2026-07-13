package uk.gov.cca.api.sectorassociation.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class SectorAssociationSchemeDTO {

    @NotNull
    private Long id;

    @NotNull(message = "{sectorAssociationScheme.umbrellaAgreement.notNull}")
    private SectorAssociationSchemeDocumentDTO umbrellaAgreement;

    @Valid
    private TargetSetDTO targetSet;

    private LocalDate umaDate;

    private String sectorDefinition;

    @NotNull
    private SchemeVersion schemeVersion;

    private boolean editable;
}
