package uk.gov.cca.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;

public interface RequestAuthorityInfoProvider {
    RequestAuthorityInfoDTO getRequestInfo(String id);
}
