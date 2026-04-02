package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilitiesFinalizationValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationPayloadValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;
    private final UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;
    private final EditedUnderlyingAgreementVariationDetailsValidatorService underlyingAgreementVariationDetailsValidatorService;
    private final EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService underlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService underlyingAgreementVariationSubmitFacilitiesContextValidatorService;
    private final EditedUnderlyingAgreementVariationApplicationReasonDataValidator underlyingAgreementVariationApplicationReasonDataValidator;
    private final UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;
    private static final UnderlyingAgreementVariationContainerMapper UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    public void validate(RequestTask requestTask) {
        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .build();
        
        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService
                .getValidationResults(unaContainer, underlyingAgreementValidationContext);

        // Validate target period details
        cca2BaselineAndTargetsValidatorService.validate(unaContainer, taskPayload.getOriginalUnderlyingAgreementContainer(), request.getCreationDate().toLocalDate());

        // Validate facilities context for variation submission step
        validationResults.add(underlyingAgreementVariationSubmitFacilitiesContextValidatorService.validate(unaContainer));
        
        // Validate rules for active (proposed) facilities
        validationResults.add(underlyingAgreementFacilitiesFinalizationValidatorService.validate(unaContainer.getUnderlyingAgreement().getFacilities()));

        // Validate previous facility ids and application reasons
        validationResults.add(underlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload));

        // Validate target unit
        validationResults.add(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(underlyingAgreementVariationDetailsValidatorService.validate(taskPayload));

        // Validate review groups and facility review groups
        validationResults.add(underlyingAgreementVariationReviewDecisionDataValidator
                .validateFacilityAndGroupReviewDecisions(taskPayload));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
