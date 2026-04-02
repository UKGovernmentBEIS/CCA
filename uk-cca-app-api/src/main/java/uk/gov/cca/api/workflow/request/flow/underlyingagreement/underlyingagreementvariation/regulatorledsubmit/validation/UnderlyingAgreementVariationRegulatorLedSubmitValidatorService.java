package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilitiesFinalizationValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.transform.UnderlyingAgreementVariationContainerMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.VariationRegulatorLedDeterminationValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRegulatorLedSubmitValidatorService {

    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;
    private final UnderlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService;
    private final UnderlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator;
    private final CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;
    private final UnderlyingAgreementVariationRegulatorLedSubmitTargetUnitDetailsValidatorService underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService;
    private final VariationRegulatorLedDeterminationValidator variationRegulatorLedDeterminationValidator;
    private final UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService;
    private final UnderlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService;
    private static final UnderlyingAgreementVariationContainerMapper MAPPER = Mappers.getMapper(UnderlyingAgreementVariationContainerMapper.class);

    public List<BusinessValidationResult> validateSubmit(final RequestTask requestTask) {
    	Request request = requestTask.getRequest();
        UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload) requestTask.getPayload();
        UnderlyingAgreementContainer unaContainer = MAPPER.toUnderlyingAgreementContainer(taskPayload);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(requestTask.getRequest().getCreationDate())
                .schemeVersion(taskPayload.getWorkflowSchemeVersion())
                .build();

        // Validate underlying agreement
        List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService
                .getValidationResults(unaContainer, underlyingAgreementValidationContext);

        // Validate facilities
        validationResults.addAll(underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService
                .validate(unaContainer, taskPayload.getFacilityChargeStartDateMap()));

        // Validate rules for active (proposed) facilities
        validationResults.add(underlyingAgreementFacilitiesFinalizationValidatorService.validate(unaContainer.getUnderlyingAgreement().getFacilities()));
        
        // Validate previous facility ids and application reasons
        validationResults.add(underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator.validate(taskPayload));

        // Validate target period details
        validationResults.add(cca2BaselineAndTargetsValidatorService.validate(
        		unaContainer, taskPayload.getOriginalUnderlyingAgreementContainer(), request.getCreationDate().toLocalDate()));

        // Validate target unit
        validationResults.add(underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService.validate(requestTask));

        // Validate Variation details
        validationResults.add(underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService.validate(taskPayload));

        // Validate determination
        validationResults.add(variationRegulatorLedDeterminationValidator.validate(taskPayload.getDetermination()));

        // Validate attachments
        validationResults.add(underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService.validate(taskPayload));

        return validationResults;
    }
}
