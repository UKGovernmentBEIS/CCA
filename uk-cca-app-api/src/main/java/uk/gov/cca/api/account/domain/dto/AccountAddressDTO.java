package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.referencedata.service.Country;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAddressDTO {

    @NotBlank
    @Size(max = 255)
    private String line1;

    @Size(max = 255)
    private String line2;

    @NotBlank
    @Size(max = 255)
    private String city;

    @NotBlank
    @Size(max = 255)
    private String postcode;

    @Size(max = 255)
    private String county;

    @NotBlank
    @Size(max = 255)
    @Country
    private String country;
}
