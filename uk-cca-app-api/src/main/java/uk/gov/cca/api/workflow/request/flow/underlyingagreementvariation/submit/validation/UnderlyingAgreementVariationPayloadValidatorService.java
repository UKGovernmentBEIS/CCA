package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationPayloadValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementVariationDetailsValidatorService underlyingAgreementVariationDetailsValidatorService;
    private final UnderlyingAgreementVariationTargetUnitDetailsValidatorService underlyingAgreementVariationTargetUnitDetailsValidatorService;
    private final UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService underlyingAgreementVariationSubmitFacilitiesContextValidatorService;
    private static final UnderlyingAgreementVariationContainerMapper UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    public void validate(RequestTask requestTask) {
        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload();
        UnderlyingAgreementContainer unaContainer = UNDERLYING_AGREEMENT_VARIATION_CONTAINER_MAPPER.toUnderlyingAgreementContainer(taskPayload);

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService.getValidationResults(unaContainer);

        // Validate facilities context for variation submission step
        validationResults.add(underlyingAgreementVariationSubmitFacilitiesContextValidatorService.validate(unaContainer));

        // Validate target unit
        validationResults.add(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate variation details
        validationResults.add(underlyingAgreementVariationDetailsValidatorService.validate(requestTask));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
