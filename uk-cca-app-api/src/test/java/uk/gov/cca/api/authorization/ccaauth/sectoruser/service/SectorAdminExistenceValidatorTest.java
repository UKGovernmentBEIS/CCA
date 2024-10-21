package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorAdminExistenceValidatorTest {

    @InjectMocks
    private SectorAdminExistenceValidator sectorAdminExistenceValidator;
    @Mock
    private CcaAuthorityRepository ccaAuthorityRepository;


    @Test
    void testValidateDeletion_NoException() {
        CcaAuthority authority = CcaAuthority.builder()
                .userId("user")
                .code("sector_user_administrator")
                .status(AuthorityStatus.ACTIVE).build();
        when(ccaAuthorityRepository.existsOtherSectorUserAdmin(authority.getUserId())).thenReturn(true);

        assertDoesNotThrow(() -> sectorAdminExistenceValidator.validateDeletion(authority));

        verify(ccaAuthorityRepository, times(1)).existsOtherSectorUserAdmin(authority.getUserId());
    }

    @Test
    void testValidateDeletion_ThrowsException_WhenNoOtherAdmins() {
        CcaAuthority authority = CcaAuthority.builder()
                .userId("user")
                .code("sector_user_administrator")
                .status(AuthorityStatus.ACTIVE).build();
        when(ccaAuthorityRepository.existsOtherSectorUserAdmin(authority.getUserId())).thenReturn(false);

        assertThrows(BusinessException.class, () -> sectorAdminExistenceValidator.validateDeletion(authority));

        verify(ccaAuthorityRepository, times(1)).existsOtherSectorUserAdmin(authority.getUserId());
    }

    @Test
    void testValidateDeletion_NoException_WhenCodeIsDifferent() {
        CcaAuthority authority = CcaAuthority.builder()
                .userId("user")
                .code("not_sector_user_administrator")
                .status(AuthorityStatus.ACTIVE).build();

        assertDoesNotThrow(() -> sectorAdminExistenceValidator.validateDeletion(authority));

        verify(ccaAuthorityRepository, never()).existsOtherSectorUserAdmin(anyString());
    }

    @Test
    void testValidateDeletion_NoException_WhenStatusIsNotActive() {
        CcaAuthority authority = CcaAuthority.builder()
                .userId("user")
                .code("sector_user_administrator")
                .status(AuthorityStatus.DISABLED).build();

        assertDoesNotThrow(() -> sectorAdminExistenceValidator.validateDeletion(authority));

        // No interactions expected with repository because the condition should not meet
        verify(ccaAuthorityRepository, never()).existsOtherSectorUserAdmin(anyString());
    }
}