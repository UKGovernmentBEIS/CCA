package uk.gov.cca.api.web.controller.facility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditUpdateDTO;
import uk.gov.cca.api.facilityaudit.domain.dto.FacilityAuditViewDTO;
import uk.gov.cca.api.facilityaudit.service.FacilityAuditService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.NetzErrorCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class FacilityAuditControllerTest {

	@InjectMocks
	private FacilityAuditController controller;

	@Mock
	private FacilityAuditService service;

	@Test
	void getFacilityAuditViewByFacilityId() {
		final Long facilityId = 123L;
		final FacilityAuditViewDTO facilityAudit = FacilityAuditViewDTO.builder()
				.comments("Comments")
				.reasons(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION))
				.build();
		final AppUser appUser = AppUser.builder()
				.roleType("REGULATOR")
				.build();

		when(service.getFacilityAuditViewByFacilityId(facilityId, appUser))
				.thenReturn(facilityAudit);

		ResponseEntity<FacilityAuditViewDTO> result = controller.getFacilityAuditViewByFacilityId(facilityId, appUser);

		assertNotNull(result);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(facilityAudit, result.getBody());
		assertNotNull(result.getBody());
		assertEquals(facilityAudit.getReasons(), result.getBody().getReasons());
	}

	@Test
	void getFacilityAuditViewByFacilityId_NOT_FOUND() {
		final Long facilityId = 123L;
		final AppUser appUser = AppUser.builder()
				.roleType("REGULATOR")
				.build();

		doThrow(new BusinessException(RESOURCE_NOT_FOUND))
				.when(service).getFacilityAuditViewByFacilityId(facilityId, appUser);

		NetzErrorCode code = assertThrows(BusinessException.class,
				() -> controller.getFacilityAuditViewByFacilityId(facilityId,appUser))
				.getErrorCode();

		assertNotNull(code);
		assertEquals(HttpStatus.NOT_FOUND, code.getHttpStatus());
	}

	@Test
	void editFacilityAuditDetailsByFacilityId() {
		final Long facilityId = 123L;
		final FacilityAuditUpdateDTO facilityAuditUpdateDTO = FacilityAuditUpdateDTO.builder()
				.comments("Comments")
				.reasons(List.of(FacilityAuditReasonType.ELIGIBILITY, FacilityAuditReasonType.SEVENTY_RULE_EVALUATION))
				.build();
		final AppUser appUser = AppUser.builder()
				.roleType("REGULATOR")
				.build();

		controller.editFacilityAuditDetailsByFacilityId(facilityId, appUser, facilityAuditUpdateDTO);

		verify(service, times(1))
				.createOrUpdateFacilityAudit(facilityId, facilityAuditUpdateDTO, appUser.getUserId());
	}
}