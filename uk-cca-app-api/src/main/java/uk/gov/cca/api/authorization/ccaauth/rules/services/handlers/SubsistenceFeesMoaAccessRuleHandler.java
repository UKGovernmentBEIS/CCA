package uk.gov.cca.api.authorization.ccaauth.rules.services.handlers;

import java.util.Map;
import java.util.Set;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaAuthorityInfoProvider;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service("subsistenceFeesMoaAccessHandler")
@RequiredArgsConstructor
public class SubsistenceFeesMoaAccessRuleHandler implements AuthorizationResourceRuleHandler {

	private final SubsistenceFeesMoaAuthorityInfoProvider subsistenceFeesMoaAuthorityInfoProvider;
    private final AppAuthorizationService appAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user,
                              String resourceId) {

    	 Pair<String, Long> moaResourceIdPair = subsistenceFeesMoaAuthorityInfoProvider.getSubsistenceFeesMoaResourceIdById(Long.parseLong(resourceId));
    	 String moaResourceId = moaResourceIdPair.getSecond().toString();
        
    	 authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            		.requestResources(addRequestResources(moaResourceIdPair, moaResourceId))
            		.build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }

	private Map<String, String> addRequestResources(Pair<String, Long> moaResourceIdPair,String moaResourceId){
		if("SECTOR_MOA".equals(moaResourceIdPair.getFirst())) {
			return Map.of(CcaResourceType.SECTOR_ASSOCIATION, moaResourceId);
		} else if("TARGET_UNIT_MOA".equals(moaResourceIdPair.getFirst())) {
			return Map.of(ResourceType.ACCOUNT, moaResourceId);
		} else {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}
}
