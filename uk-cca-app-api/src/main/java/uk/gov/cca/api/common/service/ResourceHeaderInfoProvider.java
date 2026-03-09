package uk.gov.cca.api.common.service;

import uk.gov.cca.api.common.domain.ResourceHeaderInfoDTO;

public interface ResourceHeaderInfoProvider {

    ResourceHeaderInfoDTO getResourceHeaderInfo(String resourceId);

    String getResourceType();
}
