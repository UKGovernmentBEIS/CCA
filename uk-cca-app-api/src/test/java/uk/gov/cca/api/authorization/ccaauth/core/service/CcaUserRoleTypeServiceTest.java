package uk.gov.cca.api.authorization.ccaauth.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaUserRoleType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaUserRoleTypeRepository;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class CcaUserRoleTypeServiceTest {

    @InjectMocks
    private CcaUserRoleTypeService userRoleTypeService;

    @Mock
    private CcaUserRoleTypeRepository userRoleTypeRepository;

    @Test
    void getUserRoleTypeByUserId() {
        String userId = "userId";
        CcaUserRoleType userRoleType = CcaUserRoleType.builder().userId(userId).roleType(SECTOR_USER).build();
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(SECTOR_USER).build();

        when(userRoleTypeRepository.findById(userId)).thenReturn(Optional.of(userRoleType));

        // Inject
        UserRoleTypeDTO result = userRoleTypeService.getUserRoleTypeByUserId(userId);

        // Verify
        assertEquals(userRoleTypeDTO, result);
        verify(userRoleTypeRepository, times(1)).findById(userId);
    }

    @Test
    void getUserRoleTypeByUserId_userId_not_found() {
        String userId = "userId";

        when(userRoleTypeRepository.findById(userId)).thenReturn(Optional.empty());

        // Inject
        BusinessException businessException = assertThrows(BusinessException.class, () ->
            userRoleTypeService.getUserRoleTypeByUserId(userId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);

        verify(userRoleTypeRepository, times(1)).findById(userId);
    }
}