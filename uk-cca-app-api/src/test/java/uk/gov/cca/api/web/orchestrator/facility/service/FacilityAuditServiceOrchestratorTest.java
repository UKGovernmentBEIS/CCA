package uk.gov.cca.api.web.orchestrator.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditUpdateDTO;
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacilityAuditServiceOrchestratorTest {

	@InjectMocks
	private FacilityAuditServiceOrchestrator orchestrator;

	@Mock
	private FacilityAuditService facilityAuditService;

	@Test
	void getFacilityAuditViewByFacilityId() {
		final AppUser appUser = mock(AppUser.class);
		final Long facilityId = 1L;

		orchestrator.getFacilityAuditViewByFacilityId(facilityId, appUser);

		verify(facilityAuditService, times(1))
				.getFacilityAuditViewByFacilityId(facilityId, appUser);
	}

	@Test
	void createOrUpdateFacilityAuditByFacilityId_TRUE() {

		final AppUser appUser = AppUser.builder().userId("userId").build();
		final Long facilityId = 1L;
		final FacilityAuditUpdateDTO facilityAuditUpdateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(true)
				.comments("Comments")
				.reasons(new ArrayList<>(List.of(FacilityAuditReasonType.REPORTING_DATA)))
				.build();

		orchestrator.createOrUpdateFacilityAuditByFacilityId(facilityId, facilityAuditUpdateDTO, appUser.getUserId());

		verify(facilityAuditService, times(1))
				.createOrUpdateFacilityAudit(facilityId, facilityAuditUpdateDTO, appUser.getUserId());
	}

	@Test
	void createOrUpdateFacilityAuditByFacilityId_FALSE() {

		final AppUser appUser = AppUser.builder().userId("userId").build();
		final Long facilityId = 1L;
		final FacilityAuditUpdateDTO facilityAuditUpdateDTO = FacilityAuditUpdateDTO.builder()
				.auditRequired(false)
				.build();

		orchestrator.createOrUpdateFacilityAuditByFacilityId(facilityId, facilityAuditUpdateDTO, appUser.getUserId());

		verify(facilityAuditService, times(1))
				.createOrUpdateFacilityAudit(facilityId, facilityAuditUpdateDTO, appUser.getUserId());
	}
}