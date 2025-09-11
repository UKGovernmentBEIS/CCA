package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityCertificationAccountProcessingRequestMetadata extends RequestMetadata {
    private String parentRequestId;
    private String accountBusinessId;
    private CertificationPeriodDTO certificationPeriodDetails;
}
