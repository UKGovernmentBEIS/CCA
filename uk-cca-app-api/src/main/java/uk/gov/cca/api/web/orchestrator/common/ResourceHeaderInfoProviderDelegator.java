package uk.gov.cca.api.web.orchestrator.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.service.ResourceHeaderInfoProvider;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceHeaderInfoProviderDelegator {

    private final List<ResourceHeaderInfoProvider> resourceHeaderInfoProviders;

    public Optional<ResourceHeaderInfoProvider> getResourceHeaderInfoProvider(String resourceType) {
        return resourceHeaderInfoProviders.stream()
                .filter(service -> service.getResourceType().equals(resourceType))
                .findFirst();
    }
}
