package uk.gov.cca.api.underlyingagreement.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementContainer {

    @NotEmpty
    private Map<SchemeVersion, SchemeData> schemeDataMap;

    @NotNull
    @Valid
    private UnderlyingAgreement underlyingAgreement;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    private Set<@NotNull Facility> excludedFacilities = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();
}
