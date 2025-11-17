package uk.gov.cca.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateValidator;

public interface RequestCreateByFacilityValidator extends RequestCreateValidator{

	@Transactional
    RequestCreateValidationResult validateAction(Long facilityId);
}
