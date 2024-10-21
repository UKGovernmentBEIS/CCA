package uk.gov.cca.api.authorization.ccaauth.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityWithPermissionDTO;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.exception.BusinessException;


import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class CcaAuthorityServiceTest {

    @InjectMocks
    private CcaAuthorityService authorityService;

    @Mock
    private CcaAuthorityRepository authorityRepository;

    @Mock
    private CcaAuthorityDetailsRepository authorityDetailsRepository;

    @Test
    void getActiveAuthoritiesWithAssignedPermissions() {
        String userId = "userId";
        AuthorityStatus status = AuthorityStatus.ACTIVE;
        String code = "code";
        CcaAuthorityWithPermissionDTO authority = CcaAuthorityWithPermissionDTO.builder()
                .code(code)
                .status(String.valueOf(status))
                .sectorAssociationId(1L)
                .permissions("permission1,permission2")
                .build();

        when(authorityRepository.findActiveAuthoritiesWithAssignedPermissionsByUserId(userId)).thenReturn(List.of(authority));

        // Inject
        List<CcaAuthorityDTO> res = authorityService.getActiveAuthoritiesWithAssignedPermissions(userId);

        // Verify
        assertThat(res).hasSize(1);

        verify(authorityRepository, times(1)).findActiveAuthoritiesWithAssignedPermissionsByUserId(userId);
    }

    @Test
    void findCcaAuthorityByUuidAndStatusPending() {
        String uuid = "some-uuid";
        CcaAuthority ccaAuthority = new CcaAuthority();

        when(authorityRepository.findByUuidAndStatus(uuid, AuthorityStatus.PENDING))
            .thenReturn(Optional.of(ccaAuthority));

        Optional<CcaAuthorityInfoDTO> res = authorityService.findCcaAuthorityByUuidAndStatusPending(uuid);

        assertThat(res).isPresent();

        verify(authorityRepository, times(1)).findByUuidAndStatus(uuid, AuthorityStatus.PENDING);
    }

    @Test
    void updateCcaAuthorityDetailsOrganizationName_SuccessfulUpdate() {
        CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .id(1L)
                .organisationName("test_organisation")
                .build();
        when(authorityDetailsRepository.findById(1L)).thenReturn(Optional.of(authorityDetails));

        authorityService.updateCcaAuthorityDetailsOrganisationName(1L, "test_organisation_updated");

        Assertions.assertEquals("test_organisation_updated", authorityDetails.getOrganisationName());
        verify(authorityDetailsRepository, times(1)).findById(1L);
    }

    @Test
    void updateCcaAuthorityDetailsOrganizationName_ResourceNotFound() {

        when(authorityDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> authorityService.updateCcaAuthorityDetailsOrganisationName(1L, "test_organisation_updated"));

        verify(authorityDetailsRepository, times(1)).findById(1L);
        verify(authorityDetailsRepository, times(0)).save(any(CcaAuthorityDetails.class));
    }
}