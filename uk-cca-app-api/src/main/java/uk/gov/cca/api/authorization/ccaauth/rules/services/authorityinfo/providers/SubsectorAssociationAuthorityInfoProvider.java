package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface SubsectorAssociationAuthorityInfoProvider {
    Long getSectorAssociationIdBySubsectorId(Long subsectorId);
}
