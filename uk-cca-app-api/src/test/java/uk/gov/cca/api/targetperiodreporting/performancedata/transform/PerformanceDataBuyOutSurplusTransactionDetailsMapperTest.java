package uk.gov.cca.api.targetperiodreporting.performancedata.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PerformanceDataBuyOutSurplusTransactionDetailsMapperTest {
    
    private final PerformanceDataBuyOutSurplusTransactionDetailsMapper mapper =
            new PerformanceDataBuyOutSurplusTransactionDetailsMapperImpl();
    
    @Test
    void shouldMapToPerformanceDataDetailsInfoDTO() {
        TargetUnitAccountBusinessInfoDTO account = new TargetUnitAccountBusinessInfoDTO();
        account.setBusinessId("ACC123");
        account.setName("Test Operator");
        
        SurplusBuyOutDetermination surplusBuyOutDetermination = SurplusBuyOutDetermination.builder()
                .priBuyOutCarbon(new BigDecimal("100.50"))
                .build();
        
        PerformanceDataContainer dataContainer = PerformanceDataContainer.builder()
                .surplusBuyOutDetermination(surplusBuyOutDetermination)
                .build();
        
        PerformanceDataEntity performanceData = PerformanceDataEntity.builder()
                .reportVersion(2)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(dataContainer)
                .build();
        
        PerformanceDataDetailsInfoDTO result =
                mapper.toPerformanceDataDetailsInfoDTO(performanceData, account);
        
        assertNotNull(result);
        assertEquals("ACC123", result.getAccountBusinessId());
        assertEquals("Test Operator", result.getOperatorName());
        assertEquals(2, result.getReportVersion());
        assertEquals(PerformanceDataSubmissionType.PRIMARY, result.getSubmissionType());
        assertEquals(new BigDecimal("100.50"), result.getPriBuyOutCarbon());
    }
    
    @Test
    void shouldHandleNullValues() {
        PerformanceDataEntity performanceData = PerformanceDataEntity.builder().build();
        TargetUnitAccountBusinessInfoDTO account = new TargetUnitAccountBusinessInfoDTO();
        
        PerformanceDataDetailsInfoDTO result =
                mapper.toPerformanceDataDetailsInfoDTO(performanceData, account);
        
        assertNotNull(result);
        assertNull(result.getAccountBusinessId());
        assertNull(result.getOperatorName());
        assertEquals(0, result.getReportVersion());
        assertNull(result.getSubmissionType());
        assertNull(result.getPriBuyOutCarbon());
    }
}
