package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

import java.util.List;

public interface TargetUnitAuthorityInfoProvider {

    Long getAccountSectorAssociationId(Long accountId);

    List<Long> getAllTargetUnitAccountIdsBySectorAssociationId(Long sectorAssociationId);
}
