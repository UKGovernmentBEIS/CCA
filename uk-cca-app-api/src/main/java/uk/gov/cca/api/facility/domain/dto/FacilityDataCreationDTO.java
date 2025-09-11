package uk.gov.cca.api.facility.domain.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataCreationDTO {

    @NotNull
    private String facilityId;

    @NotNull
    private Long accountId;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdDate;

    @NotNull
    private String siteName;

    @NotNull
    @Valid
    private AccountAddressDTO address;

    @NotEmpty
    Set<SchemeVersion> participatingSchemeVersions;

    private LocalDate chargeStartDate;
}
