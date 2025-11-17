package uk.gov.cca.api.facilityaudit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.FacilityAuthorizationResourceService;
import uk.gov.cca.api.facilityaudit.domain.FacilityAudit;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditUpdateDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.cca.api.facilityaudit.repository.FacilityAuditRepository;
import uk.gov.cca.api.facilityaudit.transform.FacilityAuditMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityAuditService {

	private final FacilityAuditRepository facilityAuditRepository;
	private final FacilityAuthorizationResourceService facilityAuthorizationResourceService;

	private static final FacilityAuditMapper FACILITY_AUDIT_MAPPER = Mappers.getMapper(FacilityAuditMapper.class);


	public FacilityAuditViewDTO getFacilityAuditViewByFacilityId(Long facilityId, AppUser appUser) {
		final FacilityAudit facilityAudit = this.getOrInitializeFacilityAuditByFacilityId(facilityId);
		boolean isEditable = facilityAuthorizationResourceService
				.hasUserScopeToFacility(appUser, CcaScope.EDIT_AUDIT_DATA, facilityId);

		return RoleTypeConstants.REGULATOR.equals(appUser.getRoleType()) ?
				FACILITY_AUDIT_MAPPER.toFacilityAuditViewDTO(facilityAudit, isEditable) :
				FacilityAuditViewDTO.builder()
				.auditRequired(facilityAudit.isAuditRequired())
				.editable(isEditable)
				.build();
	}

	public void createOrUpdateFacilityAudit(Long facilityId, FacilityAuditUpdateDTO dto, String userId) {
		final FacilityAudit facilityAudit = this.getOrInitializeFacilityAuditByFacilityId(facilityId);
		facilityAudit.setAuditRequired(dto.getAuditRequired());
		facilityAudit.setComments(dto.getComments());
		facilityAudit.setReasons(dto.getReasons());
		facilityAudit.setUpdatedBy(userId);
		facilityAuditRepository.save(facilityAudit);
	}

	public Set<Long> getAuditRequiredFacilityIds(Set<Long> facilityIds) {

		return facilityAuditRepository.findAllByAuditRequiredIsTrueAndFacilityIdIn(facilityIds).stream()
				.map(FacilityAudit::getFacilityId)
				.collect(Collectors.toSet());
	}

	private FacilityAudit getOrInitializeFacilityAuditByFacilityId(Long facilityId) {
		return facilityAuditRepository.findFacilityAuditByFacilityId(facilityId)
				.orElse(FacilityAudit.builder().facilityId(facilityId).build());
	}
}
