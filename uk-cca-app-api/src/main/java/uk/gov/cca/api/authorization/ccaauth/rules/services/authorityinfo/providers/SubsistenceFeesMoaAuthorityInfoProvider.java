package uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers;

import org.springframework.data.util.Pair;

public interface SubsistenceFeesMoaAuthorityInfoProvider {

	Pair<String, Long> getSubsistenceFeesMoaResourceIdById(Long moaId);
}
