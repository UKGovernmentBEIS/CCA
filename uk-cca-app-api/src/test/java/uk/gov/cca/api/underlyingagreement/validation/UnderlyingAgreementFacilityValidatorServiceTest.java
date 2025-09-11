package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.config.UnderlyingAgreementConfig;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.AgreementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Apply70Rule;
import uk.gov.cca.api.underlyingagreement.domain.facilities.EligibilityDetailsAndAuthorisation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityExtent;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.RegulatorNameType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_EVIDENCE_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_TARGETS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_TYPE;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilityValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementFacilityValidatorService validatorService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;
    
    @Mock
    private UnderlyingAgreementConfig underlyingAgreementConfig;

    @Test
    void validate() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_LIVE_with_CCA2_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.LIVE, evidenceFile, calculatorFile);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isActiveFacility(facility.getFacilityItem().getFacilityId())).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .isActiveFacility(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_exists_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(true);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_LIVE_not_exists_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.LIVE, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isActiveFacility(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .isActiveFacility(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_evidence_file_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.PDF.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_EVIDENCE_ATTACHMENT_TYPE.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_previous_facility_not_active_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(facilityDataQueryService.isActiveFacility(previousFacilityId)).thenReturn(false);
        when(facilityDataQueryService.getParticipatingFacilitySchemeVersions(previousFacilityId))
                .thenReturn(Set.of(SchemeVersion.CCA_3));
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.now().minusDays(1));

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_PREVIOUS_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verify(facilityDataQueryService, times(1)).isActiveFacility(previousFacilityId);
        verify(facilityDataQueryService, times(1)).getParticipatingFacilitySchemeVersions(previousFacilityId);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_measurement_type_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.CARBON_KG).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_TARGET_UNIT_TYPE.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_not_NOVEM_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getCca3BaselineAndTargets().getTargetComposition().setAgreementCompositionType(AgreementCompositionType.RELATIVE);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_AGREEMENT_COMPOSITION_TYPE.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_calculator_file_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.PDF.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_not_all_improvements_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityTargets().setImprovements(Map.of(
                TargetImprovementType.TP8, BigDecimal.TEN,
                TargetImprovementType.TP9, BigDecimal.TEN
        ));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_TARGETS.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_NEW_with_only_CCA2_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_previous_facility_CCA3_for_both_schema_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(facilityDataQueryService.isActiveFacility(previousFacilityId)).thenReturn(true);
        when(facilityDataQueryService.getParticipatingFacilitySchemeVersions(previousFacilityId))
                .thenReturn(Set.of(SchemeVersion.CCA_3));
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.now().minusDays(1));

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verify(facilityDataQueryService, times(1)).isActiveFacility(previousFacilityId);
        verify(facilityDataQueryService, times(1)).getParticipatingFacilitySchemeVersions(previousFacilityId);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_CCA2_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_2;
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, null);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        facility.getFacilityItem().setCca3BaselineAndTargets(null);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_scheme_greater_than_current_scheme_version_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_2;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025,1,1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage(),
                INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage());
        verify(facilityDataQueryService, times(1))
                .isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }
    
    @Test
    void validate_participating_scheme_versions_on_change_of_ownership_after_cutoff_date_not_equal_should_be_invalid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";
        
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile);
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));
        
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2027,1,1, 0, 0))
                .build();
        
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        when(facilityDataQueryService.isExistingFacilityId(facility.getFacilityItem().getFacilityId())).thenReturn(false);
        when(fileAttachmentService.getFileDTO(evidenceFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        when(facilityDataQueryService.isActiveFacility(previousFacilityId)).thenReturn(true);
        when(facilityDataQueryService.getParticipatingFacilitySchemeVersions(previousFacilityId))
                .thenReturn(Set.of(SchemeVersion.CCA_3));
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.of(2025, 1, 1));
        
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);
        
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .contains(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE.getMessage());
        
        verify(facilityDataQueryService, times(1)).isExistingFacilityId(facility.getFacilityItem().getFacilityId());
        verify(facilityDataQueryService, times(1)).isActiveFacility(previousFacilityId);
        verify(facilityDataQueryService, times(1)).getParticipatingFacilitySchemeVersions(previousFacilityId);
        verify(fileAttachmentService, times(1)).getFileDTO(evidenceFile.toString());
        verify(fileAttachmentService, times(1)).getFileDTO(calculatorFile.toString());
        verifyNoMoreInteractions(facilityDataQueryService);
    }
    
    
    private Facility createFacility(FacilityStatus status, UUID evidenceFile, UUID calculatorFile) {
        return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(UUID.randomUUID().toString())
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
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
                                .baselineData(FacilityBaselineData.builder().build())
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
