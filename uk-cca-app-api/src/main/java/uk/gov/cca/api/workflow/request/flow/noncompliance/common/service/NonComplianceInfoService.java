package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NonComplianceInfoService {

    private final RequestQueryService requestQueryService;
    private final FacilityDataQueryService facilityDataQueryService;

    public Map<String, String> getAllRelevantWorkflows(Long accountId, String requestId) {
        return requestQueryService.findByResourceTypeAndResourceIdAndTypeNotIn(Collections.emptyList(), ResourceType.ACCOUNT, String.valueOf(accountId))
                .stream()
                .filter(r -> !r.getId().equals(requestId))
                .collect(Collectors.toMap(RequestInfoDTO::getId, RequestInfoDTO::getType));
    }

    public Map<String, String> getAllRelevantFacilities(Long accountId) {
        return facilityDataQueryService.getFacilitiesByAccountId(accountId)
                .stream()
                .collect(Collectors.toMap(FacilityBaseInfoDTO::getFacilityBusinessId, FacilityBaseInfoDTO::getSiteName));
    }
}
