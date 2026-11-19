package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCertificationAccountProcessingRequestPayload extends RequestPayload {
    private String parentRequestId;
    private String accountBusinessId;
    private CertificationPeriodDTO certificationPeriodDetails;
    private TargetPeriodInfoDTO targetPeriod;
    private List<FacilityDTO> facilities;
    private UnderlyingAgreementContainer underlyingAgreement;
}
