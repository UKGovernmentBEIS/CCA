package uk.gov.cca.api.underlyingagreement.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_SECTION_DATA;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_EVIDENCE_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.AgreementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Apply70Rule;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.EligibilityDetailsAndAuthorisation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityExtent;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.RegulatorNameType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilitiesContextValidatorServiceTest {

	@InjectMocks
    private UnderlyingAgreementFacilitiesContextValidatorService validatorService;

    @Mock
    private DataValidator<Facility> validator;

    @Mock
    private UnderlyingAgreementFacilityValidatorService underlyingAgreementFacilityValidatorService; 
    
    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void validate() {
        final Facility facility1 = createFacility("facility1");
        final Facility facility2 = createFacility("facility2");
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(fileAttachmentService, times(2)).getFiles(anySet());
    }

    @Test
    void validate_not_unique_ids_not_valid() {
        final Facility facility1 = createFacility("facility");
        final Facility facility2 = createFacility("facility");

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_UNIQUE_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(fileAttachmentService, times(2)).getFiles(anySet());
    }

    @Test
    void validate_empty_facilities_not_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITIES.getMessage());
        verifyNoInteractions(validator, underlyingAgreementFacilityValidatorService);
    }

    @Test
    void validate_with_data_violation() {
        final Facility facility1 = createFacility("facility1");
        final Facility facility2 = createFacility("facility2");

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.of(new BusinessViolation("testClass", "testMessage")));
        when(validator.validate(facility2)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_SECTION_DATA.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), anyList());
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), anyList());
        verify(fileAttachmentService, times(2)).getFiles(anySet());
    }
    
    @Test
    void validate_not_valid_evidenceFileTypes() {
    	final UUID evidenceFile1 = UUID.randomUUID();
        final Facility facility1 = createFacility("facility1");
        facility1.getFacilityItem().setApply70Rule(Apply70Rule.builder()
        		.evidenceFile(evidenceFile1)
        		.build());
        final Facility facility2 = createFacility("facility2");
    	final UUID evidenceFile2 = UUID.randomUUID();
        facility2.getFacilityItem().setApply70Rule(Apply70Rule.builder()
        		.evidenceFile(evidenceFile2)
        		.build());
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();
        
        final List<FileDTO> evidenceFileDTOs = new ArrayList<>();
        evidenceFileDTOs.add(FileDTO.builder()
				.fileName("a")
				.fileType((String) FileType.PDF.getMimeTypes().toArray()[0])
				.build());
        evidenceFileDTOs.add(FileDTO.builder()
				.fileName("b")
				.fileType((String) FileType.XLS.getMimeTypes().toArray()[0])
				.build());

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(fileAttachmentService.getFiles(Set.of(evidenceFile1.toString(), evidenceFile2.toString()))).thenReturn(evidenceFileDTOs);

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_EVIDENCE_ATTACHMENT_TYPE.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(fileAttachmentService, times(1)).getFiles(Set.of(evidenceFile1.toString(), evidenceFile2.toString()));
    }
    
    @Test
    void validate_not_valid_calculatorFileTypes() {
    	final UUID evidenceFile1 = UUID.randomUUID();
    	final UUID calculatorFile1 = UUID.randomUUID();
        final Facility facility1 = createCca3FacilityAllData("facility1", FacilityStatus.LIVE, evidenceFile1, calculatorFile1);

        final UUID evidenceFile2 = UUID.randomUUID();
    	final UUID calculatorFile2 = UUID.randomUUID();
        final Facility facility2 = createCca3FacilityAllData("facility2", FacilityStatus.LIVE, evidenceFile2, calculatorFile2);
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();
        
        final List<FileDTO> evidenceFileDTOs = new ArrayList<>();
        evidenceFileDTOs.add(FileDTO.builder()
				.fileName("a")
				.fileType((String) FileType.XLSX.getMimeTypes().toArray()[0])
				.build());
        evidenceFileDTOs.add(FileDTO.builder()
				.fileName("b")
				.fileType((String) FileType.XLS.getMimeTypes().toArray()[0])
				.build());
        
        final List<FileDTO> calculatorFileDTOs = new ArrayList<>();
        calculatorFileDTOs.add(FileDTO.builder()
				.fileName("a")
				.fileType((String) FileType.PDF.getMimeTypes().toArray()[0])
				.build());
        calculatorFileDTOs.add(FileDTO.builder()
				.fileName("b")
				.fileType((String) FileType.XLS.getMimeTypes().toArray()[0])
				.build());

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(fileAttachmentService.getFiles(Set.of(evidenceFile1.toString(), evidenceFile2.toString()))).thenReturn(evidenceFileDTOs);
        when(fileAttachmentService.getFiles(Set.of(calculatorFile1.toString(), calculatorFile2.toString()))).thenReturn(calculatorFileDTOs);

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility1), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(underlyingAgreementFacilityValidatorService, times(1))
                .validate(eq(facility2), eq(container), argThat(ctx -> SchemeVersion.CCA_3.equals(ctx.getSchemeVersion())), eq(List.of()));
        verify(fileAttachmentService, times(1)).getFiles(Set.of(evidenceFile1.toString(), evidenceFile2.toString()));
    }

    private Facility createFacility(String facilityId) {
        return Facility.builder()
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId)
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(true)
                                .uketsId(UUID.randomUUID().toString())
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                .build())
                        .build())
                .build();
    }
    
    private Facility createCca3FacilityAllData(String facilityId, FacilityStatus status, UUID evidenceFile, UUID calculatorFile) {
        return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(facilityId)
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                .facilityAddress(FacilityAddressDTO.builder()
                                        .line1("Line 1")
                                        .line2("Line 2")
                                        .city("City")
                                        .county("County")
                                        .postcode("code")
                                        .country("Country")
                                        .build())
                                .build())
                        .facilityContact(TargetUnitAccountContactDTO.builder()
                                .email("xx@test.gr")
                                .firstName("First")
                                .lastName("Last")
                                .jobTitle("Job")
                                .address(AccountAddressDTO.builder()
                                        .line1("Line 1")
                                        .line2("Line 2")
                                        .city("City")
                                        .county("County")
                                        .postcode("code")
                                        .country("Country")
                                        .build())
                                .phoneNumber(PhoneNumberDTO.builder()
                                        .countryCode("30")
                                        .number("9999999999")
                                        .build())
                                .build())
                        .eligibilityDetailsAndAuthorisation(EligibilityDetailsAndAuthorisation.builder()
                                .isConnectedToExistingFacility(Boolean.TRUE)
                                .adjacentFacilityId("adjacentFacilityId")
                                .agreementType(AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS)
                                .erpAuthorisationExists(Boolean.TRUE)
                                .authorisationNumber("authorisationNumber")
                                .regulatorName(RegulatorNameType.ENVIRONMENT_AGENCY)
                                .permitFile(UUID.randomUUID())
                                .build())
                        .facilityExtent(FacilityExtent.builder()
                                .manufacturingProcessFile(UUID.randomUUID())
                                .processFlowFile(UUID.randomUUID())
                                .annotatedSitePlansFile(UUID.randomUUID())
                                .eligibleProcessFile(UUID.randomUUID())
                                .areActivitiesClaimed(Boolean.TRUE)
                                .activitiesDescriptionFile(UUID.randomUUID())
                                .build())
                        .apply70Rule(Apply70Rule.builder()
                                .energyConsumed(BigDecimal.valueOf(65))
                                .energyConsumedProvision(BigDecimal.valueOf(12))
                                .energyConsumedEligible(BigDecimal.valueOf(72.8))
                                .startDate(LocalDate.now())
                                .evidenceFile(evidenceFile)
                                .build())
                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                .targetComposition(FacilityTargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_KWH)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(FacilityBaselineData.builder().baselineDate(LocalDate.of(2022, 1, 1)).build())
                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                        .totalFixedEnergy(BigDecimal.valueOf(253.36))
                                        .hasVariableEnergy(true)
                                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                                        .variableEnergyConsumptionDataByProduct(List.of(ProductVariableEnergyConsumptionData.builder()
                                                .baselineYear(Year.of(2022))
                                                .productName("FacilityProduct1")
                                                .energy(BigDecimal.valueOf(-125.69))
                                                .throughput(BigDecimal.valueOf(56))
                                                .throughputUnit("Each")
                                                .productStatus(ProductStatus.NEW)
                                                .build()))
                                        .build())
                                .facilityTargets(FacilityTargets.builder()
                                        .improvements(Map.of(
                                                TargetImprovementType.TP7, BigDecimal.TEN,
                                                TargetImprovementType.TP8, BigDecimal.TEN,
                                                TargetImprovementType.TP9, BigDecimal.TEN
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
