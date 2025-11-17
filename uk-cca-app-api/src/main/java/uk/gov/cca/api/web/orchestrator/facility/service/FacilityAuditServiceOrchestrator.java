package uk.gov.cca.api.web.orchestrator.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditUpdateDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class FacilityAuditServiceOrchestrator {

	private final FacilityAuditService facilityAuditService;

	public FacilityAuditViewDTO getFacilityAuditViewByFacilityId(Long facilityId, AppUser appUser) {
		return facilityAuditService.getFacilityAuditViewByFacilityId(facilityId, appUser);
	}

	@Transactional
	public void createOrUpdateFacilityAuditByFacilityId(Long facilityId, FacilityAuditUpdateDTO facilityAuditUpdateDTO, String userId) {

		facilityAuditService.createOrUpdateFacilityAudit(facilityId, facilityAuditUpdateDTO, userId);
	}
}
