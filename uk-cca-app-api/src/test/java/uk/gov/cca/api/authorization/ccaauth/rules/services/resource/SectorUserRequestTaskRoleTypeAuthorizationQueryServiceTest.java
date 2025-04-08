package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.*;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorUserAuthorityService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorUserRequestTaskRoleTypeAuthorizationQueryServiceTest {

    @InjectMocks
    private SectorUserRequestTaskRoleTypeAuthorizationQueryService sectorUserRequestTaskRoleTypeAuthorizationQueryService;

    @Mock
    private SectorUserAuthorityService sectorUserAuthorityService;

    @Mock
    private SectorUserAuthorityResourceService sectorUserAuthorityResourceService;

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteria_requiresPermission_true() {
        String requestTaskType = "requestTaskType";
        Long sectorAssociationId = 1L;
        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
        		.requestResources(Map.of(
        			ResourceType.ACCOUNT, "100",
	        		CcaResourceType.SECTOR_ASSOCIATION, "1",
	        		ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name())
	        		)
        		.build();
        List<String> users = List.of("user");

        when(sectorUserAuthorityResourceService.findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(ResourceType.REQUEST_TASK, requestTaskType,
                Scope.REQUEST_TASK_EXECUTE, sectorAssociationId)).thenReturn(users);

        List<String> usersWhoCanExecuteRequestTaskTypeByAccountCriteria =
                sectorUserRequestTaskRoleTypeAuthorizationQueryService.findUsersWhoCanExecuteRequestTaskTypeByResourceCriteria
                        (requestTaskType, resourceCriteria, true);

        assertEquals(users, usersWhoCanExecuteRequestTaskTypeByAccountCriteria);
        verify(sectorUserAuthorityResourceService, times(1)).findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(ResourceType.REQUEST_TASK, requestTaskType,
                Scope.REQUEST_TASK_EXECUTE, sectorAssociationId);
    }

    @Test
    void findUsersWhoCanExecuteRequestTaskTypeByAccountCriteria_requiresPermission_false() {
        String requestTaskType = "requestTaskType";
        ResourceCriteria resourceCriteria = ResourceCriteria.builder()
        		.requestResources(Map.of(
    	        		ResourceType.ACCOUNT, "100",
    	        		CcaResourceType.SECTOR_ASSOCIATION, "1",
    	        		ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name())
    	        		)
        		.build();
        List<String> users = List.of("user");
        TargetUnitAccount targetUnitAccount = getTargetUnitAccount();

        when(sectorUserAuthorityService.findActiveSectorUsersBySectorAssociationId(targetUnitAccount.getSectorAssociationId())).thenReturn(users);

        List<String> usersWhoCanExecuteRequestTaskTypeByAccountCriteria =
                sectorUserRequestTaskRoleTypeAuthorizationQueryService.findUsersWhoCanExecuteRequestTaskTypeByResourceCriteria
                        (requestTaskType, resourceCriteria, false);

        assertEquals(users, usersWhoCanExecuteRequestTaskTypeByAccountCriteria);
        verify(sectorUserAuthorityService, times(1)).findActiveSectorUsersBySectorAssociationId(targetUnitAccount.getSectorAssociationId());
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, sectorUserRequestTaskRoleTypeAuthorizationQueryService.getRoleType());
    }

    private static TargetUnitAccount getTargetUnitAccount() {
        return TargetUnitAccount.builder()
                .id(1L)
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(1L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user1")
                .creationDate(LocalDateTime.now())
                .businessId("businessId1")
                .name("Target Unit Account 1")
                .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
                .build();
    }
}
