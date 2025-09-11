package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BuyOutSurplusRunRequestPayload extends RequestPayload {
    private String submitterId;
    private TargetPeriodDTO targetPeriodDetails;
    private BuyOutSurplusRunSummary runSummary;
    private FileInfoDTO csvFile;
    private BuyOutSurplusErrorType errorType;

    @Builder.Default
    private Map<Long, BuyOutSurplusAccountState> buyOutSurplusAccountStates = new HashMap<>();
}
