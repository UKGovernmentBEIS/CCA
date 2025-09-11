package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

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
public class SurplusCreateHistory {

    private String submitterId;

    @NotBlank
    private String submitter;

    @Size(max = 10000)
    private String comments;
}
