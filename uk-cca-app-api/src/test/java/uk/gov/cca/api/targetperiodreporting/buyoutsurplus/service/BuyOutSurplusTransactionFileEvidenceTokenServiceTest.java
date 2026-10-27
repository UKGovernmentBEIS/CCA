package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionFileEvidenceTokenServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionFileEvidenceTokenService fileEvidenceTokenService;

    @Mock
    private BuyOutSurplusTransactionQueryService buyOutSurplusTransactionQueryService;

    @Test
    void validateFileEvidenceResource() {
        final Long resourceId = 1L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        when(buyOutSurplusTransactionQueryService.getBuyOutSurplusTransactionHistoryEvidenceFiles(resourceId))
                .thenReturn(Set.of(fileEvidenceUuid, UUID.randomUUID()));

        // Invoke
        fileEvidenceTokenService.validateFileEvidenceResource(resourceId, fileEvidenceUuid);

        // Verify
        verify(buyOutSurplusTransactionQueryService, times(1))
                .getBuyOutSurplusTransactionHistoryEvidenceFiles(resourceId);
    }

    @Test
    void validateFileEvidenceResource_not_found() {
        final Long resourceId = 1L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        when(buyOutSurplusTransactionQueryService.getBuyOutSurplusTransactionHistoryEvidenceFiles(resourceId))
                .thenReturn(Set.of(UUID.randomUUID(), UUID.randomUUID()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileEvidenceTokenService.validateFileEvidenceResource(resourceId, fileEvidenceUuid));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_TRANSACTION);
        verify(buyOutSurplusTransactionQueryService, times(1))
                .getBuyOutSurplusTransactionHistoryEvidenceFiles(resourceId);
    }
}
