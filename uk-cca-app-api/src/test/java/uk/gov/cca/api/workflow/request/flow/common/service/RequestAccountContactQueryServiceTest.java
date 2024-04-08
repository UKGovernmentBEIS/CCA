package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.enumeration.AccountContactType;
import uk.gov.cca.api.account.service.AccountContactQueryService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAccountContactQueryServiceTest {

    @InjectMocks
    private RequestAccountContactQueryService service;

    @Mock
    private AccountContactQueryService accountContactQueryService;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Test
    void getRequestAccountContact() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();
        AccountContactType contactType = AccountContactType.PRIMARY;
        
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, contactType))
            .thenReturn(Optional.of("primaryUserId"));
        
        when(userAuthService.getUserByUserId("primaryUserId")).thenReturn(userInfoDTO);
        
        //invoke
        Optional<UserInfoDTO> result = service.getRequestAccountContact(request, contactType);
            
        assertThat(result.get()).isEqualTo(userInfoDTO);
        
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, contactType);
        verify(userAuthService, times(1)).getUserByUserId("primaryUserId");
    }
    
    @Test
    void getRequestAccountPrimaryContact() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();
        
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.of("primaryUserId"));
        
        when(userAuthService.getUserByUserId("primaryUserId")).thenReturn(userInfoDTO);
        
        //invoke
        Optional<UserInfoDTO> result = service.getRequestAccountPrimaryContact(request);
            
        assertThat(result.get()).isEqualTo(userInfoDTO);
        
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verify(userAuthService, times(1)).getUserByUserId("primaryUserId");
    }
    
    @Test
    void getRequestAccountPrimaryContact_not_found() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();

        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.empty());

        Optional<UserInfoDTO> result = service.getRequestAccountPrimaryContact(request);

        //invoke
        assertFalse(result.isPresent());
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verifyNoInteractions(userAuthService);
    }
    
    
}
