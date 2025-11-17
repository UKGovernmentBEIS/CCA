package uk.gov.cca.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestCreateFacilityAndAccountAndSectorResourcesService {

    private final FacilityDataQueryService facilityDataQueryService;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    public Map<String, String> createRequestResources(Long facilityId) {
        Long accountId = facilityDataQueryService.getAccountIdByFacilityId(facilityId);
        Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);
        return Map.of(
                CcaResourceType.FACILITY, facilityId.toString(),
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
        );
    }
}
