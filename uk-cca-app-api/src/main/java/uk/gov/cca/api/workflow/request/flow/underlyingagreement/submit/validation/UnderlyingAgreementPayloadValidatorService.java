package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.UnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementPayloadValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;
    private static final UnderlyingAgreementContainerMapper UNDERLYING_AGREEMENT_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);

    public void validate(RequestTask requestTask) {
        UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload();
        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService.getValidationResults(unaContainer);

        // Validate target unit
        validationResults.add(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
