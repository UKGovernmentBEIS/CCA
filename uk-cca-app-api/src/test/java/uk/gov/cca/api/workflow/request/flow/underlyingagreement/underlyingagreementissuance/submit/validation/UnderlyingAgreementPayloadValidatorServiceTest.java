package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.validation.EditedUnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementPayloadValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementPayloadValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;

    @Mock
    private EditedUnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;
    
    @Mock
    private UnderlyingAgreementFacilityAgainstCca2EndDateValidatorService underlyingAgreementCca2EndDateValidatorService;


    @Test
    void validate() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();

        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025,1,1,0,0))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request).build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(workflowSchemeVersion)
                .requestCreationDate(request.getCreationDate())
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(container, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(container))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementCca2EndDateValidatorService.validate(container.getUnderlyingAgreement().getFacilities()))
        		.thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validate(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validateEmpty(container);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementCca2EndDateValidatorService, times(1))
        		.validate(container.getUnderlyingAgreement().getFacilities());
    }

    @Test
    void validate_not_valid() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();
        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025,1,1,0,0))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(workflowSchemeVersion)
                .requestCreationDate(request.getCreationDate())
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.invalid(List.of()));
        when(underlyingAgreementValidatorService.getValidationResults(container, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(container))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementCca2EndDateValidatorService.validate(container.getUnderlyingAgreement().getFacilities()))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT);
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validateEmpty(container);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementCca2EndDateValidatorService, times(1))
				.validate(container.getUnderlyingAgreement().getFacilities());
    }
}
