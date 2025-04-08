package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

public interface FacilityAuthorityInfoProvider {

    Long getAccountIdByFacilityId(String facilityId);
}
