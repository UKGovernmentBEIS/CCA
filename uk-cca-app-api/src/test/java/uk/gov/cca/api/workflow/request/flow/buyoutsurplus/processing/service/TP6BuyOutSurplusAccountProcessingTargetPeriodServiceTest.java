package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation.BuyOutSurplusViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutCalculatedDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.SurplusCalculatedDetails;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.service.CalculateWorkingDaysService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TP6BuyOutSurplusAccountProcessingTargetPeriodServiceTest {

    @InjectMocks
    private TP6BuyOutSurplusAccountProcessingTargetPeriodService tp6BuyOutSurplusAccountProcessingTargetPeriodService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Mock
    private BuyOutSurplusAccountProcessingOfficialNoticeService buyOutSurplusAccountProcessingOfficialNoticeService;
    
    @Mock
    private CalculateWorkingDaysService calculateWorkingDaysService;

    @Mock
    private BuyOutSurplusManagementService buyOutSurplusManagementService;

    @Mock
    private RequestService requestService;

    @Test
    void doProcess_PRIMARY_TARGET_MET() throws BuyOutSurplusAccountProcessingException {
        final Long accountId = 1L;
        final long performanceDataId = 11L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String submitterId = "regulator";
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(targetPeriodType)
                .buyOutEndDate(LocalDate.of(2025, 7, 1))
                .isCurrent(true)
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("parentRequestId")
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .metadata(metadata)
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .targetPeriodDetails(targetPeriodDetails)
                        .submitterId(submitterId)
                        .build())
                .build();

        final PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceDataBuyOutSurplusDetailsDTO.builder()
                .performanceDataId(performanceDataId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .bankedSurplus(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .totalPriBuyOutCarbon(BigDecimal.ZERO)
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(performanceDataId)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                        .invoicedSurplusGained(BigDecimal.ZERO)
                        .priBuyOutCost(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                        .invoicedPreviousPaidFees(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                        .build())
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousPayments = List.of();
        final BuyOutSurplusAccountProcessingRequestMetadata expectedMetadata =
                BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .parentRequestId("parentRequestId")
                        .performanceDataReportVersion(1)
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .performanceDataId(performanceDataId)
                        .build();
        final String actionType = CcaRequestActionType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED;
        final TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload actionPayload =
                TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .details(BuyOutSurplusDetails.builder()
                                .targetPeriodType(targetPeriodType)
                                .performanceDataReportVersion(1)
                                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                                .runId("parentRequestId")
                                .build())
                        .surplusCalculatedDetails(SurplusCalculatedDetails.builder()
                                .surplusGained(BigDecimal.ZERO)
                                .previousPaidFees(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                                .overPaymentFee(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                                .build())
                        .build();

        when(buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType))
                .thenReturn(previousPayments);
        when(accountPerformanceDataStatusQueryService.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType))
                .thenReturn(performanceReportDetails);

        // Invoke
        tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(request, accountState);

        // Verify
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getBuyOutSurplus())
                .isEqualTo(buyOutSurplus);
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceReportDetails);
        assertThat((BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata())
                .isEqualTo(expectedMetadata);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);
        verify(buyOutSurplusManagementService, times(1))
                .createBankSurplus(eq(accountId), any(), any());
        verify(buyOutSurplusManagementService, times(1))
                .saveBuyOutSurplusProcessedData(performanceDataId);
        verifyNoInteractions(targetUnitAccountNoticeRecipients, buyOutSurplusAccountProcessingOfficialNoticeService);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, actionType, submitterId);
        verifyNoMoreInteractions(buyOutSurplusManagementService);
    }

    @Test
    void doProcess_PRIMARY_BUY_OUT_REQUIRED() throws BuyOutSurplusAccountProcessingException {
        final Long accountId = 1L;
        final long performanceDataId = 11L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String submitterId = "regulator";
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(targetPeriodType)
                .buyOutEndDate(LocalDate.of(2025, 7, 1))
                .isCurrent(true)
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("parentRequestId")
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .metadata(metadata)
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .targetPeriodDetails(targetPeriodDetails)
                        .submitterId(submitterId)
                        .build())
                .build();

        final String transactionCode = "transaction";
        final List<DefaultNoticeRecipient> recipients = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceDataBuyOutSurplusDetailsDTO.builder()
                .performanceDataId(performanceDataId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .bankedSurplus(BigDecimal.ZERO)
                .surplusGained(BigDecimal.ZERO)
                .priBuyOutCarbon(BigDecimal.valueOf(43))
                .priBuyOutCost(BigDecimal.valueOf(150))
                .totalPriBuyOutCarbon(BigDecimal.valueOf(43))
                .build();
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                .invoicedSurplusGained(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                .invoicedPreviousPaidFees(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .invoicedPaymentDeadline(LocalDate.of(2025, 7, 1))
                .chargeType(BuyOutSurplusChargeType.FEE)
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(performanceDataId)
                .buyOutSurplusContainer(container)
                .transactionCode(transactionCode)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .fileDocumentUuid("notice")
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousPayments = List.of();
        final BuyOutSurplusAccountProcessingRequestMetadata expectedMetadata =
                BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .parentRequestId("parentRequestId")
                        .performanceDataReportVersion(1)
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .performanceDataId(performanceDataId)
                        .transactionCode(transactionCode)
                        .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().uuid("notice").build();
        final String actionType = CcaRequestActionType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED;
        final TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload actionPayload =
                TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .details(BuyOutSurplusDetails.builder()
                                .targetPeriodType(targetPeriodType)
                                .performanceDataReportVersion(1)
                                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                .transactionCode(transactionCode)
                                .officialNotice(officialNotice)
                                .dueDate(LocalDate.of(2025, 7, 1))
                                .runId("parentRequestId")
                                .build())
                        .buyOutCalculatedDetails(BuyOutCalculatedDetails.builder()
                                .priBuyOutCarbon(BigDecimal.valueOf(43).setScale(2, RoundingMode.HALF_UP))
                                .priBuyOutCost(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                                .previousPaidFees(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                                .buyOutFee(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                                .build())
                        .defaultContacts(recipients)
                        .build();

        when(accountPerformanceDataStatusQueryService.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType))
                .thenReturn(performanceReportDetails);
        when(buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType))
                .thenReturn(previousPayments);
        when(buyOutSurplusManagementService.generateTransactionCode(targetPeriodDetails))
                .thenReturn(transactionCode);
        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId))
                .thenReturn(recipients);
        when(buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, PerformanceDataSubmissionType.PRIMARY, BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, transactionCode))
                .thenReturn(officialNotice);

        // Invoke
        tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(request, accountState);

        // Verify
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getBuyOutSurplus())
                .isEqualTo(buyOutSurplus);
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceReportDetails);
        assertThat((BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata())
                .isEqualTo(expectedMetadata);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);
        verify(buyOutSurplusManagementService, times(1))
                .createBankSurplus(eq(accountId), any(), any());
        verify(targetUnitAccountNoticeRecipients, times(1)).getDefaultNoticeRecipients(accountId);
        verify(buyOutSurplusManagementService, times(1)).generateTransactionCode(targetPeriodDetails);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .generateOfficialNotice(request, PerformanceDataSubmissionType.PRIMARY, BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, transactionCode);
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusTransaction(buyOutSurplus);
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusProcessedData(performanceDataId);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, actionType, submitterId);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .sendOfficialNotice(request, officialNotice, null);
        verifyNoMoreInteractions(buyOutSurplusManagementService, buyOutSurplusAccountProcessingOfficialNoticeService);
    }

    @Test
    void doProcess_SECONDARY_TARGET_MET() throws BuyOutSurplusAccountProcessingException {
        final Long accountId = 1L;
        final long performanceDataId = 11L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String submitterId = "regulator";
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(targetPeriodType)
                .buyOutEndDate(LocalDate.of(2025, 7, 1))
                .isCurrent(true)
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("parentRequestId")
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build(),
                        RequestResource.builder()
                        .resourceType(ResourceType.CA)
                        .resourceId(CompetentAuthorityEnum.ENGLAND.name())
                        .build())
                )
                .metadata(metadata)
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .targetPeriodDetails(targetPeriodDetails)
                        .submitterId(submitterId)
                        .build())
                .build();

        final String transactionCode = "transaction";
        final List<DefaultNoticeRecipient> recipients = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceDataBuyOutSurplusDetailsDTO.builder()
                .performanceDataId(performanceDataId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                .bankedSurplus(BigDecimal.ZERO)
                .surplusGained(BigDecimal.TWO)
                .priBuyOutCarbon(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ZERO)
                .totalPriBuyOutCarbon(BigDecimal.ZERO)
                .build();
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                .invoicedSurplusGained(BigDecimal.TWO)
                .priBuyOutCost(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .invoicedPreviousPaidFees(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(performanceDataId)
                .buyOutSurplusContainer(container)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .transactionCode(transactionCode)
                .fileDocumentUuid("notice")
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousPayments = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(15)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );
        final BuyOutSurplusAccountProcessingRequestMetadata expectedMetadata =
                BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .parentRequestId("parentRequestId")
                        .performanceDataReportVersion(1)
                        .tpOutcome(TargetPeriodResultType.TARGET_MET)
                        .performanceDataId(performanceDataId)
                        .transactionCode(transactionCode)
                        .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().uuid("notice").build();
        final FileInfoDTO refundClaimForm = FileInfoDTO.builder().uuid("refund").build();
        final String actionType = CcaRequestActionType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED;
        final TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload actionPayload =
                TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .details(BuyOutSurplusDetails.builder()
                                .targetPeriodType(targetPeriodType)
                                .performanceDataReportVersion(1)
                                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                                .transactionCode(transactionCode)
                                .officialNotice(officialNotice)
                                .runId("parentRequestId")
                                .build())
                        .surplusCalculatedDetails(SurplusCalculatedDetails.builder()
                                .surplusGained(BigDecimal.TWO)
                                .previousPaidFees(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                                .overPaymentFee(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                                .build())
                        .defaultContacts(recipients)
                        .build();

        when(accountPerformanceDataStatusQueryService.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType))
                .thenReturn(performanceReportDetails);
        when(buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType))
                .thenReturn(previousPayments);
        when(buyOutSurplusManagementService.generateTransactionCode(targetPeriodDetails))
                .thenReturn(transactionCode);
        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId))
                .thenReturn(recipients);
        when(buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, PerformanceDataSubmissionType.SECONDARY, BuyOutSurplusPaymentStatus.AWAITING_REFUND, transactionCode))
                .thenReturn(officialNotice);
        when(buyOutSurplusAccountProcessingOfficialNoticeService.generateRefundOfficialNotice(request))
                .thenReturn(refundClaimForm);
        when(calculateWorkingDaysService.addWorkingDays(any(LocalDate.class), eq(30), eq(CompetentAuthorityEnum.ENGLAND)))
        		.thenReturn(LocalDate.now().plusDays(30));

        // Invoke
        tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(request, accountState);

        // Verify
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getBuyOutSurplus())
                .isEqualTo(buyOutSurplus);
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceReportDetails);
        assertThat((BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata())
                .isEqualTo(expectedMetadata);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);
        verify(buyOutSurplusManagementService, times(1)).terminateAwaitPayments(previousPayments, metadata);
        verify(buyOutSurplusManagementService, times(1))
                .createBankSurplus(eq(accountId), any(), any());
        verify(targetUnitAccountNoticeRecipients, times(1)).getDefaultNoticeRecipients(accountId);
        verify(buyOutSurplusManagementService, times(1)).generateTransactionCode(targetPeriodDetails);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .generateOfficialNotice(request, PerformanceDataSubmissionType.SECONDARY, BuyOutSurplusPaymentStatus.AWAITING_REFUND, transactionCode);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1)).generateRefundOfficialNotice(request);
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusTransaction(buyOutSurplus);
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusProcessedData(performanceDataId);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload, actionType, submitterId);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .sendOfficialNotice(request, officialNotice, refundClaimForm);
        verify(calculateWorkingDaysService, times(1)).addWorkingDays(any(LocalDate.class), eq(30), eq(CompetentAuthorityEnum.ENGLAND));
    }

    @Test
    void doProcess_SECONDARY_BUY_OUT_REQUIRED() throws BuyOutSurplusAccountProcessingException {
        final Long accountId = 1L;
        final long performanceDataId = 11L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String submitterId = "regulator";
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(targetPeriodType)
                .buyOutEndDate(LocalDate.of(2025, 7, 1))
                .isCurrent(true)
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("parentRequestId")
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build(),
                        RequestResource.builder()
                        .resourceType(ResourceType.CA)
                        .resourceId(CompetentAuthorityEnum.ENGLAND.name())
                        .build())
                )
                .metadata(metadata)
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .targetPeriodDetails(targetPeriodDetails)
                        .submitterId(submitterId)
                        .build())
                .build();

        final String transactionCode = "transaction";
        final List<DefaultNoticeRecipient> recipients = List.of(
                DefaultNoticeRecipient.builder()
                        .name("Responsible Last Name")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceDataBuyOutSurplusDetailsDTO.builder()
                .performanceDataId(performanceDataId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .bankedSurplus(BigDecimal.ZERO)
                .surplusGained(BigDecimal.TWO)
                .priBuyOutCarbon(BigDecimal.valueOf(43))
                .priBuyOutCost(BigDecimal.valueOf(150))
                .totalPriBuyOutCarbon(BigDecimal.valueOf(43))
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(performanceDataId)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                        .invoicedSurplusGained(BigDecimal.TWO)
                        .priBuyOutCost(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                        .invoicedPreviousPaidFees(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .build())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .transactionCode(transactionCode)
                .fileDocumentUuid("notice")
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata expectedMetadata =
                BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .parentRequestId("parentRequestId")
                        .performanceDataReportVersion(1)
                        .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                        .performanceDataId(performanceDataId)
                        .transactionCode(transactionCode)
                        .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousPayments = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(15)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );
        final FileInfoDTO officialNotice = FileInfoDTO.builder().uuid("notice").build();
        final String actionType = CcaRequestActionType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED;

        when(accountPerformanceDataStatusQueryService.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType))
                .thenReturn(performanceReportDetails);
        when(buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType))
                .thenReturn(previousPayments);
        when(buyOutSurplusManagementService.generateTransactionCode(targetPeriodDetails))
                .thenReturn(transactionCode);
        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId))
                .thenReturn(recipients);
        when(buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, PerformanceDataSubmissionType.SECONDARY, BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, transactionCode))
                .thenReturn(officialNotice);
        when(calculateWorkingDaysService.addWorkingDays(any(LocalDate.class), eq(30), eq(CompetentAuthorityEnum.ENGLAND)))
				.thenReturn(LocalDate.now().plusDays(30));

        // Invoke
        tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(request, accountState);

        // Verify
        final BuyOutSurplusResult actual = ((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getBuyOutSurplus();

        assertThat(actual.getPerformanceDataId()).isEqualTo(buyOutSurplus.getPerformanceDataId());
        assertThat(actual.getPaymentStatus()).isEqualTo(buyOutSurplus.getPaymentStatus());
        assertThat(actual.getTransactionCode()).isEqualTo(transactionCode);
        assertThat(actual.getFileDocumentUuid()).isEqualTo(buyOutSurplus.getFileDocumentUuid());
        assertThat(actual.getBuyOutSurplusContainer().getInvoicedBuyOutFee())
                .isEqualTo(buyOutSurplus.getBuyOutSurplusContainer().getInvoicedBuyOutFee());
        assertThat(actual.getBuyOutSurplusContainer().getInvoicedSurplusGained())
                .isEqualTo(buyOutSurplus.getBuyOutSurplusContainer().getInvoicedSurplusGained());
        assertThat(actual.getBuyOutSurplusContainer().getPriBuyOutCost())
                .isEqualTo(buyOutSurplus.getBuyOutSurplusContainer().getPriBuyOutCost());
        assertThat(actual.getBuyOutSurplusContainer().getInvoicedPreviousPaidFees())
                .isEqualTo(buyOutSurplus.getBuyOutSurplusContainer().getInvoicedPreviousPaidFees());
        assertThat(actual.getBuyOutSurplusContainer().getInvoicedPaymentDeadline()).isInTheFuture();
        assertThat(((BuyOutSurplusAccountProcessingRequestPayload) request.getPayload()).getPerformanceData())
                .isEqualTo(performanceReportDetails);
        assertThat((BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata())
                .isEqualTo(expectedMetadata);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);
        verify(buyOutSurplusManagementService, times(1)).terminateAwaitPayments(previousPayments, metadata);
        verify(buyOutSurplusManagementService, times(1))
                .createBankSurplus(eq(accountId), any(), any());
        verify(targetUnitAccountNoticeRecipients, times(1)).getDefaultNoticeRecipients(accountId);
        verify(buyOutSurplusManagementService, times(1)).generateTransactionCode(targetPeriodDetails);
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .generateOfficialNotice(request, PerformanceDataSubmissionType.SECONDARY, BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, transactionCode);
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusTransaction(any());
        verify(buyOutSurplusManagementService, times(1)).saveBuyOutSurplusProcessedData(performanceDataId);
        verify(requestService, times(1)).addActionToRequest(eq(request), any(), eq(actionType), eq(submitterId));
        verify(buyOutSurplusAccountProcessingOfficialNoticeService, times(1))
                .sendOfficialNotice(request, officialNotice, null);
        verify(calculateWorkingDaysService, times(1)).addWorkingDays(any(LocalDate.class), eq(30), eq(CompetentAuthorityEnum.ENGLAND));
        verifyNoMoreInteractions(buyOutSurplusAccountProcessingOfficialNoticeService);
    }

    @Test
    void doProcess_exception() {
        final Long accountId = 1L;
        final long performanceDataId = 11L;
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(targetPeriodType)
                .buyOutEndDate(LocalDate.of(2025, 7, 1))
                .isCurrent(true)
                .build();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("parentRequestId")
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .metadata(metadata)
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .targetPeriodDetails(targetPeriodDetails)
                        .build())
                .build();

        final PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = PerformanceDataBuyOutSurplusDetailsDTO.builder()
                .performanceDataId(performanceDataId)
                .reportVersion(1)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .energyCarbonUnit(MeasurementType.ENERGY_KWH)
                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                .bankedSurplus(BigDecimal.ZERO)
                .surplusGained(BigDecimal.TWO)
                .priBuyOutCarbon(BigDecimal.valueOf(43))
                .priBuyOutCost(BigDecimal.valueOf(150))
                .totalPriBuyOutCarbon(BigDecimal.valueOf(43))
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousPayments = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(120)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        when(accountPerformanceDataStatusQueryService.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType))
                .thenReturn(performanceReportDetails);
        when(buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType))
                .thenReturn(previousPayments);

        // Invoke
        BuyOutSurplusAccountProcessingException ex = assertThrows(BuyOutSurplusAccountProcessingException.class, () ->
                tp6BuyOutSurplusAccountProcessingTargetPeriodService.processBuyOutSurplus(request, accountState));

        // Verify
        assertThat(ex.getError()).isEqualTo(BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PREVIOUS_PAID_FEES_FAILED);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);
        verifyNoInteractions(targetUnitAccountNoticeRecipients, buyOutSurplusAccountProcessingOfficialNoticeService,
                buyOutSurplusManagementService, requestService);
    }

    @Test
    void getType() {
        assertThat(tp6BuyOutSurplusAccountProcessingTargetPeriodService.getType()).isEqualTo(TargetPeriodType.TP6);
    }
}
