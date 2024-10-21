package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.cca.api.common.exception.CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityDeletionServiceTest {

	@InjectMocks
    private SectorUserAuthorityDeletionService sectorUserAuthorityDeletionService;

    @Mock
    private SectorAdminExistenceValidator validator;

    @Mock
    private CcaAuthorityRepository ccaAuthorityRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Mock
    private CcaAuthorityDetailsRepository authorityDetailsRepository;
    
	@Test
    void deleteSectorUserByUserIdAndSectorAssociation() {
        final Long sectorAssociationId = 1L;
        final String userId = "userId2";

        final CcaAuthority ccaAuthority = CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .sectorAssociationId(sectorAssociationId)
                .build();

        when(ccaAuthorityRepository.findByUserId(userId)).thenReturn(List.of(ccaAuthority));

        sectorUserAuthorityDeletionService.deleteSectorUserByUserIdAndSectorAssociation(userId, sectorAssociationId);
        
        verify(validator, times(1)).validateDeletion(ccaAuthority);
        verify(ccaAuthorityRepository, times(1)).findByUserId(userId);
        verify(authorityDetailsRepository, times(1)).deleteById(ccaAuthority.getId());
        verify(eventPublisher, times(1)).publishEvent(SectorUserDeletionEvent.builder()
                .userId(userId).sectorAssociationId(sectorAssociationId).existCcaAuthoritiesOnOtherSectorAssociations(false).build());
    }

    @Test
    void deleteSectorUserByUserIdAndSectorAssociation_user_not_related_to_sector_association() {
        final String userId = "userId";
        final Long sectorAssociationId = 1L;
        final CcaAuthority ccaAuthority = CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .sectorAssociationId(2L)
                .build();

        when(ccaAuthorityRepository.findByUserId(userId)).thenReturn(List.of(ccaAuthority));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> sectorUserAuthorityDeletionService.deleteSectorUserByUserIdAndSectorAssociation(userId, sectorAssociationId));

        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verify(ccaAuthorityRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(authorityDetailsRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deleteSectorUserByUserIdAndSectorAssociation_do_not_delete_user() {
        final String userId = "userId";
        final Long sectorAssociationId = 1L;

        final CcaAuthority ccaAuthority1 = CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .sectorAssociationId(sectorAssociationId)
                .build();

        final CcaAuthority ccaAuthority2 = CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .sectorAssociationId(2L)
                .build();

        when(ccaAuthorityRepository.findByUserId(userId)).thenReturn(List.of(ccaAuthority1, ccaAuthority2));

        sectorUserAuthorityDeletionService.deleteSectorUserByUserIdAndSectorAssociation(userId, sectorAssociationId);

        verify(ccaAuthorityRepository, times(1)).findByUserId(userId);
        verify(authorityDetailsRepository, times(1)).deleteById(ccaAuthority1.getId());
        verify(eventPublisher, times(1)).publishEvent(SectorUserDeletionEvent.builder()
                .userId(userId).sectorAssociationId(sectorAssociationId).existCcaAuthoritiesOnOtherSectorAssociations(true).build());
    }
}
