package uk.gov.cca.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;
import uk.gov.cca.api.account.repository.TargetUnitAccountIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountIdentifierServiceTest {


    @InjectMocks
    private TargetUnitAccountIdentifierService targetUnitAccountIdentifierService;

    @Mock
    private TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;

    @Test
    void incrementAndGet() {
        long identifierId = 1L;
        long sectorAssociationId = 1L;
        TargetUnitAccountIdentifier identifier = TargetUnitAccountIdentifier.builder().id(1).accountId(identifierId).build();

        when(targetUnitAccountIdentifierRepository.findTargetUnitAccountIdentifierBySectorAssociationId(sectorAssociationId)).thenReturn(Optional.of(identifier));

        // Invoke
        Long result = targetUnitAccountIdentifierService.incrementAndGet(sectorAssociationId);

        // Verify
        assertThat(result).isEqualTo(identifierId + 1);
        verify(targetUnitAccountIdentifierRepository, times(1)).findTargetUnitAccountIdentifierBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void incrementAndGet_not_found() {
        Long sectorAssociationId = 1L;

        doThrow(new BusinessException((RESOURCE_NOT_FOUND))).when(targetUnitAccountIdentifierRepository)
                .findTargetUnitAccountIdentifierBySectorAssociationId(sectorAssociationId);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> targetUnitAccountIdentifierService.incrementAndGet(sectorAssociationId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(targetUnitAccountIdentifierRepository, times(1)).findTargetUnitAccountIdentifierBySectorAssociationId(sectorAssociationId);
        verifyNoMoreInteractions(targetUnitAccountIdentifierRepository);
    }
}
