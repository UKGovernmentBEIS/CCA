package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface TargetUnitAuthorityInfoProvider {

    Long getAccountSectorAssociationId(Long accountId);
}
