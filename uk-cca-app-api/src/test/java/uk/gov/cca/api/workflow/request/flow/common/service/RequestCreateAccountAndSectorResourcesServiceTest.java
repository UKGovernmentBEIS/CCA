package uk.gov.cca.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;

@ExtendWith(MockitoExtension.class)
class RequestCreateAccountAndSectorResourcesServiceTest {

	@InjectMocks
    private RequestCreateAccountAndSectorResourcesService cut;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void createRequestResources() {
    	Long accountId = 1L;
    	Long sectorId = 2L;
    	
    	when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);
    	
    	var result = cut.createRequestResources(accountId);
    	
    	assertThat(result).isEqualTo(Map.of(
    			ResourceType.ACCOUNT, accountId.toString(), 
				CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
    			));
    	
    	verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
    }
}
