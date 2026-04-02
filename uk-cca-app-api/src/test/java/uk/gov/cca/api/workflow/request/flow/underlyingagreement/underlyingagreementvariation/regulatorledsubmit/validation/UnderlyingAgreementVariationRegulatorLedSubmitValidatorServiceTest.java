package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilitiesFinalizationValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.VariationRegulatorLedDeterminationValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitValidatorService service;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator;

    @Mock
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitTargetUnitDetailsValidatorService underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService;

    @Mock
    private VariationRegulatorLedDeterminationValidator variationRegulatorLedDeterminationValidator;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService;
    
    @Mock
    private UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;

    @Test
    void validateSubmit() {
        final LocalDateTime creationDate = LocalDateTime.now();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreement una = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .build();
        final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final Set<UUID> files = Set.of(UUID.randomUUID());
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "attachment");
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .files(files)
                .build();
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(una)
                                .build())
                        .determination(determination)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .originalUnderlyingAgreementContainer(originalUnderlyingAgreementContainer)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .creationDate(creationDate)
                        .build())
                .payload(taskPayload)
                .build();

        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(una)
                .build();
        final UnderlyingAgreementValidationContext validationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(creationDate)
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(unaContainer, validationContext))
                .thenReturn(validationResults);
        when(underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService.validate(unaContainer, facilityChargeStartDateMap))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(cca2BaselineAndTargetsValidatorService.validate(unaContainer, originalUnderlyingAgreementContainer, creationDate.toLocalDate()))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(variationRegulatorLedDeterminationValidator.validate(determination))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(unaContainer.getUnderlyingAgreement().getFacilities()))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        List<BusinessValidationResult> result = service.validateSubmit(requestTask);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isTrue();
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(unaContainer, validationContext);
        verify(underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService, times(1))
                .validate(unaContainer, facilityChargeStartDateMap);
        verify(underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
        verify(cca2BaselineAndTargetsValidatorService, times(1))
                .validate(unaContainer, originalUnderlyingAgreementContainer, creationDate.toLocalDate());
        verify(underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService, times(1))
                .validate(taskPayload);
        verify(variationRegulatorLedDeterminationValidator, times(1))
                .validate(determination);
        verify(underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService, times(1))
                .validate(taskPayload);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1))
				.validate(unaContainer.getUnderlyingAgreement().getFacilities());
    }

    @Test
    void validateSubmit_not_valid() {
        final LocalDateTime creationDate = LocalDateTime.now();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreement una = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(false).build())
                .build();
        final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final Set<UUID> files = Set.of(UUID.randomUUID());
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "attachment");
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .files(files)
                .build();
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(una)
                                .build())
                        .determination(determination)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .originalUnderlyingAgreementContainer(originalUnderlyingAgreementContainer)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .creationDate(creationDate)
                        .build())
                .payload(taskPayload)
                .build();

        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(una)
                .build();
        final UnderlyingAgreementValidationContext validationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(creationDate)
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(unaContainer, validationContext))
                .thenReturn(validationResults);
        when(underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService.validate(unaContainer, facilityChargeStartDateMap))
                .thenReturn(List.of(BusinessValidationResult.valid()));
        when(underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(cca2BaselineAndTargetsValidatorService.validate(unaContainer, originalUnderlyingAgreementContainer, creationDate.toLocalDate()))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(variationRegulatorLedDeterminationValidator.validate(determination))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.invalid(List.of()));
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(unaContainer.getUnderlyingAgreement().getFacilities()))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        List<BusinessValidationResult> result = service.validateSubmit(requestTask);

        // Verify
        assertThat(result.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(unaContainer, validationContext);
        verify(underlyingAgreementVariationRegulatorLedSubmitFacilitiesContextValidatorService, times(1))
                .validate(unaContainer, facilityChargeStartDateMap);
        verify(underlyingAgreementVariationRegulatorLedSubmitApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
        verify(cca2BaselineAndTargetsValidatorService, times(1))
                .validate(unaContainer, originalUnderlyingAgreementContainer, creationDate.toLocalDate());
        verify(underlyingAgreementRegulatorLedSubmitTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(underlyingAgreementVariationRegulatorLedSubmitVariationDetailsValidatorService, times(1))
                .validate(taskPayload);
        verify(variationRegulatorLedDeterminationValidator, times(1))
                .validate(determination);
        verify(underlyingAgreementVariationRegulatorLedSubmitAttachmentsExistValidatorService, times(1))
                .validate(taskPayload);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1))
				.validate(unaContainer.getUnderlyingAgreement().getFacilities());
    }
}
