package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;
import uk.gov.cca.api.facility.repository.FacilityIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
public class FacilityIdentifierServiceTest {

    @InjectMocks
    private FacilityIdentifierService facilityIdentifierService;

    @Mock
    private FacilityIdentifierRepository facilityIdentifierRepository;

    @Test
    void incrementAndGet() {
        Long identifierId = 1L;
        Long sectorAssociationId = 1L;
        FacilityIdentifier identifier = FacilityIdentifier.builder().id(1L).facilityId(identifierId).build();

        when(facilityIdentifierRepository.findFacilityIdentifierBySectorAssociationId(sectorAssociationId)).thenReturn(Optional.of(identifier));

        Long result = facilityIdentifierService.incrementAndGet(sectorAssociationId);

        assertThat(result).isEqualTo(identifierId + 1);
        verify(facilityIdentifierRepository, times(1)).findFacilityIdentifierBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void incrementAndGet_not_found() {
        Long sectorAssociationId = 1L;
        doThrow(new BusinessException((RESOURCE_NOT_FOUND))).when(facilityIdentifierRepository)
                .findFacilityIdentifierBySectorAssociationId(sectorAssociationId);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> facilityIdentifierService.incrementAndGet(sectorAssociationId));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(facilityIdentifierRepository, times(1)).findFacilityIdentifierBySectorAssociationId(sectorAssociationId);
        verifyNoMoreInteractions(facilityIdentifierRepository);
    }
}
