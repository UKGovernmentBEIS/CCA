package uk.gov.cca.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Optional;

public interface AccountAuthorityInfoProvider {
    CompetentAuthorityEnum getAccountCa(Long accountId);

    Optional<Long> getAccountVerificationBodyId(Long accountId);
}
