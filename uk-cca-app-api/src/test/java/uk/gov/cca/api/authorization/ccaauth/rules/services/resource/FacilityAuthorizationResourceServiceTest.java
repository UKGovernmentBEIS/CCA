package uk.gov.cca.api.authorization.ccaauth.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_FACILITY_AUDIT_EDIT;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class FacilityAuthorizationResourceServiceTest {

	@InjectMocks
	private FacilityAuthorizationResourceService service;

	@Mock
	private ResourceScopePermissionService resourceScopePermissionService;

	@Mock
	private AppAuthorizationService appAuthorizationService;


	@Test
	void hasUserScopeToFacility_with_permission() {
		ResourceScopePermission requiredPermission = ResourceScopePermission.builder()
				.permission(PERM_FACILITY_AUDIT_EDIT)
				.build();
		AppUser authUser = AppUser.builder().roleType(REGULATOR).build();
		Long facilityId = 1L;
		String scope = CcaScope.EDIT_AUDIT_DATA;
		AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
				.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
				.permission(requiredPermission.getPermission())
				.build();

		when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.FACILITY,
				authUser.getRoleType(), scope)).thenReturn(Optional.of(requiredPermission));

		boolean result = service.hasUserScopeToFacility(authUser, scope, facilityId);

		assertTrue(result);
		verify(appAuthorizationService, times(1))
				.authorize(authUser, authorizationCriteria);
	}

	@Test
	void hasUserScopeToFacility_without_permission() {

		AppUser authUser = AppUser.builder().roleType(REGULATOR).build();
		Long facilityId = 1L;
		String scope = CcaScope.EDIT_AUDIT_DATA;

		when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.FACILITY,
				authUser.getRoleType(), scope)).thenReturn(Optional.empty());

		boolean result = service.hasUserScopeToFacility(authUser, scope, facilityId);

		assertFalse(result);
		verify(appAuthorizationService, times(0))
				.authorize(authUser, null);
	}

	@Test
	void hasUserScopeToFacility_throws_exception() {
		ResourceScopePermission requiredPermission = ResourceScopePermission.builder()
				.permission(PERM_FACILITY_AUDIT_EDIT)
				.build();
		AppUser authUser = AppUser.builder().roleType(OPERATOR).build();
		Long facilityId = 1L;
		String scope = CcaScope.EDIT_AUDIT_DATA;
		AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
				.requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
				.permission(requiredPermission.getPermission())
				.build();

		when(resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(CcaResourceType.FACILITY,
				authUser.getRoleType(), scope)).thenReturn(Optional.of(requiredPermission));

		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
				.when(appAuthorizationService).authorize(authUser, authorizationCriteria);

		boolean result = service.hasUserScopeToFacility(authUser, scope, facilityId);

		assertFalse(result);
		verify(appAuthorizationService, times(1))
				.authorize(authUser, authorizationCriteria);
	}
}