package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface SubsectorAssociationSchemeAuthorityInfoProvider {
    Long getSectorAssociationIdBySubsectorSchemeId(Long schemeId);
}
