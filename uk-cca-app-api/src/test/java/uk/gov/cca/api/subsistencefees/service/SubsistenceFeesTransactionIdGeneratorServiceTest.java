package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.MoaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesTransactionIdGeneratorServiceTest {

    @InjectMocks
    private SubsistenceFeesTransactionIdGeneratorService transactionIdGeneratorService;

    @Mock
    private SubsistenceFeesTransactionIdentifierService transactionIdentifierService;

    @Test
    void generateTransactionIdForSectorMOAs() {
        when(transactionIdentifierService.incrementAndGet(MoaType.SECTOR_MOA)).thenReturn(11200L);

        // invoke
        String transactionIdForSectorMOAs = transactionIdGeneratorService.generateTransactionIdForSectorMOAs();

        // verify
        assertThat(transactionIdForSectorMOAs).isEqualTo("CCACM11200");
    }

    @Test
    void generateTransactionIdForTargetUnitMOAs() {
        when(transactionIdentifierService.incrementAndGet(MoaType.TARGET_UNIT_MOA)).thenReturn(11200L);

        // invoke
        String transactionIdForTargetUnitMOAs = transactionIdGeneratorService.generateTransactionIdForTargetUnitMOAs();

        // verify
        assertThat(transactionIdForTargetUnitMOAs).isEqualTo("CCATM11200");
    }
}
