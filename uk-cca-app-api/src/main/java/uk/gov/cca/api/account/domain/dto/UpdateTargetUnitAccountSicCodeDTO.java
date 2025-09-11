package uk.gov.cca.api.account.domain.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
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

	@Size(max = 4)
    private List<@NotBlank @Size(max = 255) String> sicCodes;
}
