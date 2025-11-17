package uk.gov.cca.api.facility.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDate;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataUpdateDTO {

    @NotNull
    private String facilityBusinessId;

    @NotNull
    private String siteName;

    @NotNull
    @Valid
    private FacilityAddressDTO facilityAddress;

    @PastOrPresent
    private LocalDate closedDate;

    @NotEmpty
    private Set<SchemeVersion> participatingSchemeVersions;
}
