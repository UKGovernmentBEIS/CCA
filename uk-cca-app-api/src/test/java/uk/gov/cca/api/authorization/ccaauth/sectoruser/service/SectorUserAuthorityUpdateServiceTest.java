package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityUpdateWrapperDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserNotificationGateway;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityUpdateServiceTest {

	@InjectMocks
    private SectorUserAuthorityUpdateService updateService;

    @Mock
    private SectorUserAuthorityUpdateValidator validator;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Mock
    private SectorUserAuthorityService authorityService;
    
    @Mock
    private SectorUserNotificationGateway notificationGateway;
    
    @Mock
    private CcaAuthorityDetailsRepository authorityDetailsRepository;
    
    @Mock
    private CcaAuthorityRepository authorityRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Test
    void updateSectorUserAuthorityDetailsTest_updateContactType() {
        Long sectorAssociationId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        String authorityUuid = "uuid";
        CcaAuthority existingAuthority = createAuthority(userId, roleCode, sectorAssociationId, AuthorityStatus.PENDING, authorityUuid);
        CcaAuthorityDetails existingAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(existingAuthority)
                .contactType(ContactType.SECTOR_ASSOCIATION).build();

        when(authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId)).thenReturn(existingAuthorityDetails);

        //invoke
        updateService.updateSectorUserAuthorityDetails(sectorAssociationId, userId, ContactType.CONSULTANT, "Giant", true);

        //verify
        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityDetailsRepository, times(1)).save(authorityCaptor.capture());

        CcaAuthorityDetails authorityDetails = authorityCaptor.getValue();

        assertThat(authorityDetails).isNotNull();
        assertThat(authorityDetails.getContactType()).isEqualTo(ContactType.CONSULTANT);
        assertThat(authorityDetails.getOrganisationName()).isEqualTo("Giant");

    }

    @Test
    void updateSectorUserAuthorityDetailsTest_notUpdateContactType() {
        Long sectorAssociationId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        String authorityUuid = "uuid";
        CcaAuthority existingAuthority = createAuthority(userId, roleCode, sectorAssociationId, AuthorityStatus.PENDING, authorityUuid);
        CcaAuthorityDetails existingAuthorityDetails = CcaAuthorityDetails.builder()
                .authority(existingAuthority)
                .contactType(ContactType.SECTOR_ASSOCIATION).build();

        when(authorityService.getSectorUserAuthorityDetails(userId, sectorAssociationId)).thenReturn(existingAuthorityDetails);

        //invoke
        updateService.updateSectorUserAuthorityDetails(sectorAssociationId, userId, ContactType.CONSULTANT, "Giant", false);

        //verify
        ArgumentCaptor<CcaAuthorityDetails> authorityCaptor = ArgumentCaptor.forClass(CcaAuthorityDetails.class);
        verify(authorityDetailsRepository, times(1)).save(authorityCaptor.capture());

        CcaAuthorityDetails authorityDetails = authorityCaptor.getValue();

        assertThat(authorityDetails).isNotNull();
        assertThat(authorityDetails.getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertThat(authorityDetails.getOrganisationName()).isEqualTo("Giant");
    }
    
    @Test
    void UpdateSectorUserAuthorities_EmptyInputTest() {
        SectorUserAuthorityUpdateWrapperDTO wrapperDTO = new SectorUserAuthorityUpdateWrapperDTO(new ArrayList<>());

        updateService.updateSectorUserAuthorities(wrapperDTO.getSectorUserAuthorityUpdateDTOList(), 1L);

        verify(validator, never()).validateUpdate(any(), anyLong());
        verify(authorityRepository, never()).save(any());
    }
    
    @Test
    void updateSectorUserAuthorities_SuccessfulUpdateTest() {
        List<SectorUserAuthorityUpdateDTO> updateDTOList = new ArrayList<>();
        SectorUserAuthorityUpdateDTO toUpdateAuthority = new SectorUserAuthorityUpdateDTO();
        Long sectorAssociationId = 1L;
        toUpdateAuthority.setUserId("1");
        toUpdateAuthority.setAuthorityStatus(AuthorityStatus.ACTIVE);
        toUpdateAuthority.setRoleCode("sector_user_basic_user");
        updateDTOList.add(toUpdateAuthority);
        SectorUserAuthorityUpdateWrapperDTO wrapperDTO = new SectorUserAuthorityUpdateWrapperDTO(updateDTOList);

        CcaAuthority existingAuthority = new CcaAuthority();
        existingAuthority.setStatus(AuthorityStatus.ACCEPTED);
        existingAuthority.setCode("sector_user_administrator");

        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().code("sector_user_administrator").build());
        roles.add(Role.builder().code("sector_user_basic_user").build());

        when(roleRepository.findByType(SECTOR_USER)).thenReturn(roles);
        when(authorityService.getSectorUserAuthority("1", sectorAssociationId)).thenReturn(existingAuthority);
        List<NewUserActivated> result = updateService.updateSectorUserAuthorities(wrapperDTO.getSectorUserAuthorityUpdateDTOList(), 1L);

        assertEquals(1, result.size());
        assertEquals("sector_user_basic_user", result.get(0).getRoleCode());


        verify(validator, times(1)).validateUpdate(updateDTOList, sectorAssociationId);
        verify(authorityRepository, times(1)).save(existingAuthority);
        verify(eventPublisher, times(0)).publishEvent(any());
    }
    
    private CcaAuthority createAuthority(
    		String userId, String roleCode, Long sectorAssociationId, AuthorityStatus authorityStatus, String authorityUuid) {
        return CcaAuthority.builder()
                .id(1L)
                .userId(userId)
                .code(roleCode)
                .sectorAssociationId(sectorAssociationId)
                .status(authorityStatus)
                .authorityPermissions(new ArrayList<>())
                .uuid(authorityUuid)
                .build();
    }
}
