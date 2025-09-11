package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountProcessingServiceTest {

    @InjectMocks
    private FacilityCertificationAccountProcessingService facilityCertificationAccountProcessingService;

    @Mock
    private RequestService requestService;

    @Mock
    private BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private FacilityCertificationService facilityCertificationService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(buyOutSurplusProcessedDataQueryService.getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionByPerformanceData(performanceDataId))
                .thenReturn(Optional.empty());

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isEqualTo(3L);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusQueryService, times(1))
                .getBuyOutSurplusTransactionByPerformanceData(performanceDataId);
        verify(facilityCertificationService, times(1))
                .certifyFacilities(new HashSet<>(facilityIds), certificationPeriod.getId(), certificationPeriod.getStartDate());
    }

    @Test
    void doProcess_paid() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final BuyOutSurplusTransactionInfoDTO info = BuyOutSurplusTransactionInfoDTO.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(buyOutSurplusProcessedDataQueryService.getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(info));

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isEqualTo(3L);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusQueryService, times(1))
                .getBuyOutSurplusTransactionByPerformanceData(performanceDataId);
        verify(facilityCertificationService, times(1))
                .certifyFacilities(new HashSet<>(facilityIds), certificationPeriod.getId(), certificationPeriod.getStartDate());
    }

    @Test
    void doProcess_await_payment() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final BuyOutSurplusTransactionInfoDTO info = BuyOutSurplusTransactionInfoDTO.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(buyOutSurplusProcessedDataQueryService.getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(info));

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusQueryService, times(1))
                .getBuyOutSurplusTransactionByPerformanceData(performanceDataId);
        verifyNoInteractions(facilityCertificationService);
    }

    @Test
    void doProcess_no_buyout_surplus() throws BpmnExecutionException {
        final String requestId = "requestId";
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(buyOutSurplusProcessedDataQueryService.getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.empty());

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verifyNoInteractions(buyOutSurplusQueryService, facilityCertificationService);
    }

    @Test
    void doProcess_empty_facilities() {
        final String requestId = "requestId";
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(List.of())
                .build();

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class,
                () -> facilityCertificationAccountProcessingService.doProcess(requestId, accountState));

        // Verify
        assertThat(ex.getErrors()).containsExactly(CcaErrorCode.NO_FACILITIES_FOR_ACCOUNT.getMessage());
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verifyNoInteractions(requestService, buyOutSurplusProcessedDataQueryService,
                buyOutSurplusQueryService, facilityCertificationService);
    }
}
