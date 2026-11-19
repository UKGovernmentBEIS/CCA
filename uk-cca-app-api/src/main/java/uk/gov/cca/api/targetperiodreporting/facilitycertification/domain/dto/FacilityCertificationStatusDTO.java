package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityCertificationStatusDTO {

    private Long certificationPeriodId;

    private SchemeVersion schemeVersion;

    private FacilityCertificationStatus status;

    private LocalDate startDate;

    private CertificationPeriodType certificationPeriod;

    private LocalDate certificationPeriodStartDate;

    private LocalDate certificationPeriodEndDate;
}
