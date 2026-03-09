package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.transform.UnderlyingAgreementContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.validation.EditedUnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementPayloadValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;
    private final UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService underlyingAgreementCca2EndDateValidatorService;
    private final EditedUnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;
    private static final UnderlyingAgreementContainerMapper UNDERLYING_AGREEMENT_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementContainerMapper.class);

    public void validate(RequestTask requestTask) {
        UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload();
        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .build();
        
        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService
                .getValidationResults(unaContainer, underlyingAgreementValidationContext);

        // Validate target period details
        validationResults.add(cca2BaselineAndTargetsValidatorService.validateEmpty(unaContainer));

        // Validate target unit
        validationResults.add(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask));
        
        // Validate CCA2 end date related rules for facilities
        validationResults.add(underlyingAgreementCca2EndDateValidatorService.validate(unaContainer.getUnderlyingAgreement().getFacilities()));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
