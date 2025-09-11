package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificationPeriodInfoDTO {

    @NotNull
    private Long id;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
