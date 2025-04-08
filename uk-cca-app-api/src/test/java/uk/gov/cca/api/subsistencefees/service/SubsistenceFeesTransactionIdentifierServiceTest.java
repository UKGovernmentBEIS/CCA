package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesTransactionIdentifier;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesTransactionIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubsistenceFeesTransactionIdentifierServiceTest {

    @InjectMocks
    private SubsistenceFeesTransactionIdentifierService transactionIdentifierService;

    @Mock
    private SubsistenceFeesTransactionIdentifierRepository transactionIdentifierRepository;

    @Test
    void incrementAndGet() {
        final MoaType moaType = MoaType.SECTOR_MOA;
        final SubsistenceFeesTransactionIdentifier transactionIdentifier = SubsistenceFeesTransactionIdentifier.builder()
                .id(1)
                .transactionId(1199L)
                .moaType(moaType)
                .build();

        // invoke
        when(transactionIdentifierRepository.findByMoaType(moaType)).thenReturn(Optional.of(transactionIdentifier));

        Long identifier1 = transactionIdentifierService.incrementAndGet(moaType);
        Long identifier2 = transactionIdentifierService.incrementAndGet(moaType);

        // verify
        assertThat(identifier1).isEqualTo(1200);
        assertThat(identifier2).isEqualTo(1201);
    }

    @Test
    void incrementAndGet_error() {
        final MoaType moaType = MoaType.SECTOR_MOA;

        when(transactionIdentifierRepository.findByMoaType(moaType)).thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> transactionIdentifierService.incrementAndGet(moaType));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
        verify(transactionIdentifierRepository, times(1)).findByMoaType(moaType);
    }

}
