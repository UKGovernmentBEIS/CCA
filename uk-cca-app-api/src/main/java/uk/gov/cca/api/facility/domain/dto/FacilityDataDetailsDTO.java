package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataDetailsDTO {

    private String facilityId;

    private FacilityDataStatus status;

    private LocalDate chargeStartDate;

    private String siteName;

    private LocalDate schemeExitDate;

    private AccountAddressDTO address;
}
