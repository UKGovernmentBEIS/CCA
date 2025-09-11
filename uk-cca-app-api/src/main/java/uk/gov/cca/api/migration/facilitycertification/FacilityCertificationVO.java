package uk.gov.cca.api.migration.facilitycertification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityCertificationVO {
    private Long id;
    private String facilityId;
    private Long certificationPeriodId;
    private FacilityCertificationStatus certificationStatus;
    private LocalDate startDate;
    private CertificationPeriodType certificationPeriodType;
}
