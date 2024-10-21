package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.AgreementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Apply70Rule;
import uk.gov.cca.api.underlyingagreement.domain.facilities.EligibilityDetailsAndAuthorisation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityExtent;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.RegulatorNameType;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilitiesContextValidatorServiceTest {

	@InjectMocks
    private UnderlyingAgreementFacilitiesContextValidatorService validatorService;

    @Mock
    private DataValidator<Facility> validator;

    @Mock
    private FileAttachmentService fileAttachmentService;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void validate_file_types_correct() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isExistingFacilityId(facility1.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isExistingFacilityId(facility2.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isActiveFacility("previousFacilityId")).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
        		.thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(facilityDataQueryService, times(2)).isActiveFacility(anyString());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility1.getFacilityItem().getFacilityId());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility2.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(2)).getFileDTO(evidenceFile.toString());
    }
    
    @Test
    void validate_existing_and_adjacent_facility_ids_not_exist() {
    	final String previousFacilityId = "previousFacilityId";
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isExistingFacilityId(facility1.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isExistingFacilityId(facility2.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isActiveFacility(previousFacilityId)).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
        		.thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsOnly(
        		UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID.getMessage(),
        		UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(fileAttachmentService, times(2)).getFileDTO(evidenceFile.toString());
        verify(facilityDataQueryService, times(2)).isActiveFacility(anyString());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility1.getFacilityItem().getFacilityId());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility2.getFacilityItem().getFacilityId());
    }
    
    @Test
    void validate_second_facility_incorrect_evidence_type() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.NEW, evidenceFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, permitFile);
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isExistingFacilityId(facility1.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isExistingFacilityId(facility2.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isActiveFacility("previousFacilityId")).thenReturn(true);
        when(fileAttachmentService.getFileDTO(permitFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.PDF.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
        		.thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
        		UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_ATTACHMENT_TYPE.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(fileAttachmentService, times(1)).getFileDTO(permitFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(facilityDataQueryService, times(2)).isActiveFacility(anyString());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility1.getFacilityItem().getFacilityId());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility2.getFacilityItem().getFacilityId());
    }
    
    @Test
    void validate_no_facilities_invalid() {
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
        		UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITIES.getMessage());
        verifyNoInteractions(fileAttachmentService);
        verifyNoInteractions(facilityDataQueryService);
    }

    @Test
    void validate_status_new_but_exist_not_valid() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isExistingFacilityId(facility1.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isExistingFacilityId(facility2.getFacilityItem().getFacilityId())).thenReturn(true);
        when(facilityDataQueryService.isActiveFacility("previousFacilityId")).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(facilityDataQueryService, times(2)).isActiveFacility(anyString());
        verify(fileAttachmentService, times(2)).getFileDTO(evidenceFile.toString());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility1.getFacilityItem().getFacilityId());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility2.getFacilityItem().getFacilityId());
    }

    @Test
    void validate_status_live_but_not_exist_not_valid() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile);
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isActiveFacility(facility1.getFacilityItem().getFacilityId())).thenReturn(true);
        when(facilityDataQueryService.isActiveFacility(facility2.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isActiveFacility("previousFacilityId")).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(facilityDataQueryService, times(4)).isActiveFacility(anyString());
        verify(fileAttachmentService, times(2)).getFileDTO(evidenceFile.toString());
    }

    @Test
    void validate_same_ids_not_valid() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        facility2.getFacilityItem().setFacilityId(facility1.getFacilityItem().getFacilityId());
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        when(validator.validate(facility1)).thenReturn(Optional.empty());
        when(validator.validate(facility2)).thenReturn(Optional.empty());
        when(facilityDataQueryService.isExistingFacilityId(facility2.getFacilityItem().getFacilityId())).thenReturn(false);
        when(facilityDataQueryService.isActiveFacility("previousFacilityId")).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_FACILITY_ID.getMessage());
        verify(validator, times(1)).validate(facility1);
        verify(validator, times(1)).validate(facility2);
        verify(facilityDataQueryService, times(2))
                .isExistingFacilityId(facility1.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(2)).getFileDTO(evidenceFile.toString());
    }
    
    private Facility createFacility(FacilityStatus status, UUID permitFile, UUID evidenceFile) {
		return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(UUID.randomUUID().toString())
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                                .previousFacilityId("previousFacilityId")
                                .facilityAddress(AccountAddressDTO.builder()
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
                                .permitFile(permitFile)
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
                        .build())
    			.build();
	}
}
