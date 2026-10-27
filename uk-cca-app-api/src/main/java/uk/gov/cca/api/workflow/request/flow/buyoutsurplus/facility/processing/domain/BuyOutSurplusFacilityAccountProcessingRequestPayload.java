package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingRequestPayload extends RequestPayload {

	private String submitterId;
	private LocalDate creationDate;
    private SchemeVersion schemeVersion;
	private TargetPeriodType triggeringTargetPeriodType;
    private List<TargetPeriodInfoDTO> targetPeriodsDetails;
    private Map<TargetPeriodType, LocalDate> invoicedPaymentDeadlinePerTP;
    private boolean applyPrimaryRules;
    private TargetUnitAccountDetailsDTO accountDetails;
    private List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults;
    private List<DefaultNoticeRecipient> defaultContacts;
    private Map<TargetPeriodType, FileInfoDTO> buyOutOfficialNotices;
    private Map<TargetPeriodType, FileInfoDTO> refundOfficialNotices;
    private FileInfoDTO refundClaimForm;
}
