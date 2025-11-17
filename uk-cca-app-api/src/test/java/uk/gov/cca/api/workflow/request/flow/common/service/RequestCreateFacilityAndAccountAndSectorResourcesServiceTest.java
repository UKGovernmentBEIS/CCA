package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestCreateFacilityAndAccountAndSectorResourcesServiceTest {

    @InjectMocks
    private RequestCreateFacilityAndAccountAndSectorResourcesService service;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void createRequestResources() {
        Long facilityId = 1L;
        Long accountId = 2L;
        Long sectorId = 3L;

        when(facilityDataQueryService.getAccountIdByFacilityId(facilityId)).thenReturn(accountId);
        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorId);

        var result = service.createRequestResources(facilityId);

        assertThat(result).isEqualTo(Map.of(
                CcaResourceType.FACILITY, facilityId.toString(),
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
        ));

        verify(facilityDataQueryService, times(1)).getAccountIdByFacilityId(facilityId);
        verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
    }

}
