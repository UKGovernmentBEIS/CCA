package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTargetUnitAccountSicCodeDTO {

    @Size(max = 255)
    private String sicCode;
}
