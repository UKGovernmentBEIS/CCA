package uk.gov.cca.api.facility.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataUpdateDTO {

    @NotNull
    private String facilityId;

    @NotNull
    private String siteName;

    @NotNull
    @Valid
    private AccountAddressDTO facilityAddress;

    @PastOrPresent
    private LocalDate closedDate;

}
