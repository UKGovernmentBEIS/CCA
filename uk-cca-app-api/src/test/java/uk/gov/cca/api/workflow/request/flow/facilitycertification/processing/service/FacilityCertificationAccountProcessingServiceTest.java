package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunResourceTypeDelegatorService;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    private FacilityCertificationRunResourceTypeDelegatorService facilityCertificationRunResourceTypeDelegatorService;

    @Mock
    private FacilityCertificationService facilityCertificationService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .facilityIds(List.of(1L, 2L, 3L, 4L, 5L))
                .build();

        final Set<Long> facilityIds = Set.of(1L, 2L, 3L);

        when(facilityCertificationRunResourceTypeDelegatorService.getFacilitiesEligibleForCertification(certificationPeriod, accountState))
                .thenReturn(facilityIds);

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(request, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isEqualTo(3L);
        verify(facilityCertificationRunResourceTypeDelegatorService, times(1))
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);
        verify(facilityCertificationService, times(1))
                .certifyFacilities(facilityIds, certificationPeriod.getId(), certificationPeriod.getStartDate());
    }

    @Test
    void doProcess_no_certified() throws BpmnExecutionException {
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final Request request = Request.builder()
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .certificationPeriodDetails(certificationPeriod)
                        .build())
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .facilityIds(List.of(1L, 2L, 3L, 4L, 5L))
                .build();

        when(facilityCertificationRunResourceTypeDelegatorService.getFacilitiesEligibleForCertification(certificationPeriod, accountState))
                .thenReturn(Set.of());

        // Invoke
        facilityCertificationAccountProcessingService.doProcess(request, accountState);

        // Verify
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verify(facilityCertificationRunResourceTypeDelegatorService, times(1))
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);
        verifyNoInteractions(facilityCertificationService);
    }

    @Test
    void doProcess_empty_facilities() {
        final Request request = Request.builder().id("requestId").build();
        final long performanceDataId = 10L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(List.of())
                .build();

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class,
                () -> facilityCertificationAccountProcessingService.doProcess(request, accountState));

        // Verify
        assertThat(ex.getErrors()).containsExactly(CcaErrorCode.NO_FACILITIES_FOR_ACCOUNT.getMessage());
        assertThat(accountState.getFacilitiesCertified()).isZero();
        verifyNoInteractions(requestService, facilityCertificationRunResourceTypeDelegatorService, facilityCertificationService);
    }

    @Test
    void complete() {
        final String requestId = "requestId";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .facilitiesCertified(10L)
                .build();

        final String parentRequestId = "parentRequestId";
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .parentRequestId(parentRequestId)
                        .build())
                .build();
        FacilityCertificationRunRequestPayload runRequestPayload = FacilityCertificationRunRequestPayload.builder()
                .facilityCertificationAccountStates(new HashMap<>())
                .build();
        final Request parentRequest = Request.builder()
                .id(requestId)
                .payload(runRequestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        // Invoke
        facilityCertificationAccountProcessingService.complete(requestId, accountState);

        // Verify
        assertThat(runRequestPayload.getFacilityCertificationAccountStates()).isNotEmpty().containsKey(1L);
        assertThat(runRequestPayload.getFacilityCertificationAccountStates().get(1L).isSucceeded()).isTrue();
        assertThat(runRequestPayload.getFacilityCertificationAccountStates().get(1L).getFacilitiesCertified()).isEqualTo(10L);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(parentRequestId);
    }

    @Test
    void complete_with_errors() {
        final String requestId = "requestId";
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(1L)
                .facilitiesCertified(10L)
                .errors(List.of("error1", "error2"))
                .build();

        final String parentRequestId = "parentRequestId";
        final Request request = Request.builder()
                .id(requestId)
                .metadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .parentRequestId(parentRequestId)
                        .build())
                .build();
        FacilityCertificationRunRequestPayload runRequestPayload = FacilityCertificationRunRequestPayload.builder()
                .facilityCertificationAccountStates(new HashMap<>())
                .build();
        final Request parentRequest = Request.builder()
                .id(requestId)
                .payload(runRequestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        // Invoke
        facilityCertificationAccountProcessingService.complete(requestId, accountState);

        // Verify
        assertThat(runRequestPayload.getFacilityCertificationAccountStates()).isNotEmpty().containsKey(1L);
        assertThat(runRequestPayload.getFacilityCertificationAccountStates().get(1L).isSucceeded()).isFalse();
        assertThat(runRequestPayload.getFacilityCertificationAccountStates().get(1L).getFacilitiesCertified()).isZero();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(parentRequestId);
    }
}
