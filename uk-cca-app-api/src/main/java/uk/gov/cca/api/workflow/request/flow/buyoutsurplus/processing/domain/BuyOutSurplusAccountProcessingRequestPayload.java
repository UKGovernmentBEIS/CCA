package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOutSurplusAccountProcessingRequestPayload extends RequestPayload {
    private TargetUnitAccountDetailsDTO accountDetails;
}
