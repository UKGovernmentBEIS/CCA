package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SectorAssociationTargetUnitAccountsServiceOrchestratorTest {

	@Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @InjectMocks
    private SectorAssociationTargetUnitAccountsServiceOrchestrator orchestrator;
    
    @Test
    void getTargetUnitAccountsWithSiteContact() {
    	final AppUser user = AppUser.builder().build();
    	final long sectorAssociationId = 1L;
        final int page = 0;
        final int pageSize = 30;
        AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder().paging(PagingRequest.builder()
        		.pageNumber(page)
        		.pageSize(pageSize)
        		.build()).build();
        TargetUnitAccountInfoResponseDTO responseDTO = TargetUnitAccountInfoResponseDTO.builder().editable(true).build();
        // When
        when(targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, accountSearchCriteria))
        		.thenReturn(responseDTO);
        
        // Invoke
        TargetUnitAccountInfoResponseDTO result = 
        		orchestrator.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, accountSearchCriteria);

        // Verify
        assertEquals(responseDTO, result);
        verify(targetUnitAccountSiteContactService, times(1))
        		.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, accountSearchCriteria);
    }
    
    @Test
    void updateTargetUnitAccountSiteContacts() {
    	final AppUser user = AppUser.builder().build();
    	final long sectorAssociationId = 1L;
        final List<TargetUnitAccountSiteContactDTO> contacts = List.of();
        
        // Invoke
        orchestrator.updateTargetUnitAccountSiteContacts(user, sectorAssociationId, contacts);

        // Verify
        verify(targetUnitAccountSiteContactService, times(1))
        		.updateTargetUnitAccountSiteContacts(user, sectorAssociationId, contacts);
    }
}
