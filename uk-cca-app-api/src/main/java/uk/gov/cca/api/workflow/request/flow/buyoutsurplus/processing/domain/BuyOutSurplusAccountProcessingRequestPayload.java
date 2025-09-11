package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOutSurplusAccountProcessingRequestPayload extends RequestPayload {
    private String submitterId;
    private TargetPeriodDTO targetPeriodDetails;
    private TargetUnitAccountDetailsDTO accountDetails;
    private BuyOutSurplusResult buyOutSurplus;
    private PerformanceDataBuyOutSurplusDetailsDTO performanceData;
    private boolean isTransactionRequired;
    private List<DefaultNoticeRecipient> defaultContacts;
    private FileInfoDTO officialNotice;
    private FileInfoDTO refundClaimForm;
}
