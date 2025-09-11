package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityCertificationRunRequestMetadata extends RequestMetadata {
    private CertificationPeriodType certificationPeriodType;
    private Long totalAccounts;
    private Long failedAccounts;
    private Long facilitiesCertified;
}
