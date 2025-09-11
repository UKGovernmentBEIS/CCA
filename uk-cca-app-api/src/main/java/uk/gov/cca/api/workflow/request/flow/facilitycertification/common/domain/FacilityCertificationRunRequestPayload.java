package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCertificationRunRequestPayload extends RequestPayload {

    private CertificationPeriodDTO certificationPeriodDetails;

    private FacilityCertificationRunSummary runSummary;

    @Builder.Default
    private Map<Long, FacilityCertificationAccountState> facilityCertificationAccountStates = new HashMap<>();
}
