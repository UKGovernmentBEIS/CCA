package uk.gov.cca.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityResourceAdapter {
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public Map<CompetentAuthorityEnum, Set<RequestTaskType>> getUserScopedRequestTaskTypes(String userId) {
        Map<CompetentAuthorityEnum, Set<String>> requestTaskTypes = regulatorAuthorityResourceService.findUserScopedRequestTaskTypes(userId);
        return requestTaskTypes.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue()
                        .stream()
                        .map(RequestTaskType::valueOf)
                        .collect(Collectors.toSet())
                )
            );
    }
}
