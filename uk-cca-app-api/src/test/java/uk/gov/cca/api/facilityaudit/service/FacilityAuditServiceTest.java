package uk.gov.cca.api.facilityaudit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.FacilityAuthorizationResourceService;
import uk.gov.cca.api.facilityaudit.domain.FacilityAudit;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.cca.api.facilityaudit.repository.FacilityAuditRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope.EDIT_AUDIT_DATA;

@ExtendWith(MockitoExtension.class)
class FacilityAuditServiceTest {

	@InjectMocks
	private FacilityAuditService service;

	@Mock
	private FacilityAuditRepository repository;

	@Mock
	FacilityAuthorizationResourceService authorizationResourceService;

	@Test
	void getFacilityAuditViewByFacilityId_REGULATOR() {

		final Long facilityId = 123L;
		final FacilityAudit facilityAudit = FacilityAudit.builder()
				.facilityId(facilityId)
				.comments("Comments")
				.reasons(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION))
				.build();
		final AppUser appUser = AppUser.builder()
				.roleType("REGULATOR")
				.build();

		when(repository.findFacilityAuditByFacilityId(facilityId))
				.thenReturn(Optional.of(facilityAudit));
		when(authorizationResourceService.hasUserScopeToFacility(appUser, EDIT_AUDIT_DATA, facilityId))
				.thenReturn(true);

		final FacilityAuditViewDTO result = service.getFacilityAuditViewByFacilityId(facilityId, appUser);

		assertNotNull(result);
		assertEquals(facilityAudit.getComments(), result.getComments());
		assertEquals(facilityAudit.getReasons(), result.getReasons());
		assertEquals(facilityAudit.isAuditRequired(), result.isAuditRequired());
		assertTrue(result.isEditable());
	}

	@Test
	void getFacilityAuditViewByFacilityId_OPERATOR() {

		final Long facilityId = 123L;
		final FacilityAudit facilityAudit = FacilityAudit.builder()
				.facilityId(facilityId)
				.comments("Comments")
				.reasons(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION))
				.auditRequired(true)
				.build();
		final AppUser appUser = AppUser.builder()
				.roleType("OPERATOR")
				.build();

		when(repository.findFacilityAuditByFacilityId(facilityId))
				.thenReturn(Optional.of(facilityAudit));

		final FacilityAuditViewDTO result = service.getFacilityAuditViewByFacilityId(facilityId, appUser);

		assertNotNull(result);
		assertNull(result.getReasons());
		assertNull(result.getComments());
		assertFalse(result.isEditable());
		assertEquals(facilityAudit.isAuditRequired(), result.isAuditRequired());
	}

	@Test
	void getFacilityAuditViewByFacilityId_NOT_FOUND() {

		final Long facilityId = 123L;
		final AppUser appUser = AppUser.builder()
				.roleType("REGULATOR")
				.build();

		when(repository.findFacilityAuditByFacilityId(facilityId))
				.thenReturn(Optional.empty());
		when(authorizationResourceService.hasUserScopeToFacility(appUser, EDIT_AUDIT_DATA, facilityId))
				.thenReturn(false);

		FacilityAuditViewDTO result = service.getFacilityAuditViewByFacilityId(facilityId, appUser);

		assertNotNull(result);
		assertNull(result.getComments());
		assertTrue(result.getReasons().isEmpty());
		assertFalse(result.isAuditRequired());
		assertFalse(result.isEditable());
	}

	@Test
	void getAuditRequiredFacilityIds() {
		final FacilityAudit facilityAudit = FacilityAudit.builder()
				.facilityId(1L)
				.build();

		when(repository.findAllByAuditRequiredIsTrueAndFacilityIdIn(Set.of(1L, 2L)))
				.thenReturn(Set.of(facilityAudit));

		Set<Long> auditRequiredFacilityIds = service.getAuditRequiredFacilityIds(Set.of(1L, 2L));

        assertNotNull(auditRequiredFacilityIds);
        assertEquals(1, auditRequiredFacilityIds.size());
        assertTrue(auditRequiredFacilityIds.contains(1L));
        verify(repository, times(1))
                .findAllByAuditRequiredIsTrueAndFacilityIdIn(Set.of(1L, 2L));
    }

    @Test
    void getFacilityAuditByFacilityId() {
        final Long facilityId = 123L;
        final FacilityAudit facilityAudit = FacilityAudit.builder()
                .facilityId(facilityId)
                .comments("Comments")
                .reasons(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION))
                .build();

        when(repository.findFacilityAuditByFacilityId(facilityId)).thenReturn(Optional.of(facilityAudit));

        final FacilityAuditDTO result = service.getFacilityAuditByFacilityId(facilityId);

        assertNotNull(result);
        assertEquals(facilityAudit.getComments(), result.getComments());
        assertEquals(facilityAudit.getReasons(), result.getReasons());
    }
}