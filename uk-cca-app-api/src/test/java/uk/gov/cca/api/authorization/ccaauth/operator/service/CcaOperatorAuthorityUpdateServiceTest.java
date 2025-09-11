package uk.gov.cca.api.authorization.ccaauth.operator.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityUpdateValidator;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CcaOperatorAuthorityUpdateServiceTest {

    @Mock
    private OperatorAuthorityUpdateValidator operatorAuthorityUpdateValidator;

    @Mock
    private CcaOperatorAuthorityService ccaOperatorAuthorityService;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private CcaOperatorAuthorityUpdateService ccaOperatorAuthorityUpdateService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateAccountOperatorAuthorities_Success() {
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setAccountId(1L);
        authority.setStatus(AuthorityStatus.ACCEPTED);


        Long accountId = 1L;
        AccountOperatorAuthorityUpdateDTO dto = new AccountOperatorAuthorityUpdateDTO();
        dto.setUserId("12345678");
        dto.setAuthorityStatus(AuthorityStatus.ACTIVE);

        when(ccaOperatorAuthorityService.getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong())).thenReturn(authority);

        ccaOperatorAuthorityUpdateService.updateAccountOperatorAuthorities(Arrays.asList(dto), accountId);

        verify(operatorAuthorityUpdateValidator, times(1)).validateUpdate(anyList(), anyLong());
        verify(ccaOperatorAuthorityService, times(1)).getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong());
        verify(authorityRepository, times(1)).save(any(Authority.class));
    }

    @Test
    void testUpdateAccountOperatorAuthorities_EmptyList() {
        Long accountId = 1L;

        ccaOperatorAuthorityUpdateService.updateAccountOperatorAuthorities(Arrays.asList(), accountId);

        verify(operatorAuthorityUpdateValidator, times(0)).validateUpdate(anyList(), anyLong());
        verify(ccaOperatorAuthorityService, times(0)).getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong());
        verify(authorityRepository, times(0)).save(any(Authority.class));
    }

    @Test
    void testUpdateAccountOperatorAuthorities_NoNotify() {
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setAccountId(1L);
        authority.setStatus(AuthorityStatus.DISABLED);


        Long accountId = 1L;
        AccountOperatorAuthorityUpdateDTO dto = new AccountOperatorAuthorityUpdateDTO();
        dto.setUserId("12345678");
        dto.setAuthorityStatus(AuthorityStatus.ACTIVE);

        when(ccaOperatorAuthorityService.getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong())).thenReturn(authority);

        ccaOperatorAuthorityUpdateService.updateAccountOperatorAuthorities(Arrays.asList(dto), accountId);

        verify(operatorAuthorityUpdateValidator, times(1)).validateUpdate(anyList(), anyLong());
        verify(ccaOperatorAuthorityService, times(1)).getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong());
        verify(authorityRepository, times(1)).save(any(Authority.class));
    }

    @Test
    void testUpdateAccountOperatorAuthorities_InvalidStatuses() {
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setAccountId(1L);
        authority.setStatus(AuthorityStatus.PENDING);


        Long accountId = 1L;
        AccountOperatorAuthorityUpdateDTO dto = new AccountOperatorAuthorityUpdateDTO();
        dto.setUserId("12345678");
        dto.setAuthorityStatus(AuthorityStatus.ACTIVE);

        when(ccaOperatorAuthorityService.getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong())).thenReturn(authority);

        ccaOperatorAuthorityUpdateService.updateAccountOperatorAuthorities(Arrays.asList(dto), accountId);

        verify(operatorAuthorityUpdateValidator, times(1)).validateUpdate(anyList(), anyLong());
        verify(ccaOperatorAuthorityService, times(1)).getOperatorUserAuthorityByUserIdAndAccountId(anyString(), anyLong());
        verify(authorityRepository, times(1)).save(any(Authority.class));
    }
    
    @Test
    void updateOperatorUserAuthorityDetails_withoutContactType() {
        String organisationName = "New Organisation Name";
        String userId = "user123";
        Long accountId = 1L;
        Authority authority = new Authority();
        authority.setId(1L);
        CcaAuthorityDetails authorityDetails = new CcaAuthorityDetails();
        authorityDetails.setOrganisationName("Old Organisation Name");

        when(ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(anyString(),anyLong())).thenReturn(authorityDetails);

        ccaOperatorAuthorityUpdateService.updateOperatorUserAuthorityDetails(userId, accountId, organisationName);

        assertEquals(organisationName, authorityDetails.getOrganisationName());
    }

    @Test
    void updateOperatorUserAuthorityDetails_withContactType() {
        String userId = "user123";
        Long accountId = 1L;
        String organisationName = "New Organisation";
        ContactType contactType = ContactType.CONSULTANT;

        CcaAuthorityDetails authorityDetails = mock(CcaAuthorityDetails.class);
        when(ccaOperatorAuthorityService.getOperatorUserAuthorityDetails(anyString(),anyLong())).thenReturn(authorityDetails);
        
        ccaOperatorAuthorityUpdateService.updateOperatorUserAuthorityDetailsWithContactType(userId, accountId, organisationName, contactType);

        verify(authorityDetails).setOrganisationName(organisationName);
        verify(authorityDetails).setContactType(contactType);
    }
}