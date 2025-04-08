package uk.gov.cca.api.workflow.request.flow.performancedata.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;

@ExtendWith(MockitoExtension.class)
class PerformanceDataAccountQueryServiceTest {

    @InjectMocks
    private PerformanceDataAccountQueryService performanceDataAccountQueryService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Test
    void testGetCandidateAccountsForPerformanceDataReportingBySector() {
        final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
        final Long sectorAssociationId = 123L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final TargetPeriodDTO targetPeriodDTO = TargetPeriodDTO.builder()
                .id(456L)
                .build();

        List<TargetUnitAccountBusinessInfoDTO> mockAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(999L)
                        .businessId("ADS_1-T00001")
                        .build()
        );

        when(targetPeriodService.getTargetPeriodByBusinessId(targetPeriodType)).thenReturn(targetPeriodDTO);
        when(accountPerformanceDataStatusQueryService
                .getAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodDTO.getId(), submissionType))
                .thenReturn(mockAccounts);

        List<TargetUnitAccountBusinessInfoDTO> result = performanceDataAccountQueryService
                .getCandidateAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodType, submissionType);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(999L, result.getFirst().getAccountId());
        assertEquals("ADS_1-T00001", result.getFirst().getBusinessId());

        verify(targetPeriodService, times(1)).getTargetPeriodByBusinessId(targetPeriodType);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .getAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodDTO.getId(), submissionType);
        verifyNoMoreInteractions(targetPeriodService, accountPerformanceDataStatusQueryService);
    }
}
