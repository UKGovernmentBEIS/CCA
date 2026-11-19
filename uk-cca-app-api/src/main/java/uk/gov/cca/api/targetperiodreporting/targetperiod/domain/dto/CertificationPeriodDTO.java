package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificationPeriodDTO {
    private Long id;
    private TargetPeriodType targetPeriodType;
    private SchemeVersion schemeVersion;
    private CertificationPeriodType certificationPeriodType;
    private LocalDate certificationBatchTriggerDate;
    private LocalDate startDate;
    private LocalDate endDate;
}
