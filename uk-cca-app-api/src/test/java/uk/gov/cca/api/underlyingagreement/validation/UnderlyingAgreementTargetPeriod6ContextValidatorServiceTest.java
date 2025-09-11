package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementTargetPeriod6ContextValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementTargetPeriod6ContextValidatorService validatorService;

    @Mock
    private DataValidator<TargetPeriod6Details> validator;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void validate_NOVEM() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder().build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLS.getMimeTypes().toArray()[0]).build());
        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }

    @Test
    void validate_ABSOLUTE() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.target(BigDecimal.valueOf(7.9800000))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }
    @Test
    void validate_ABSOLUTEE_targets_invalid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                        .target(BigDecimal.valueOf(0.9800000))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }

    @Test
    void validate_ABSOLUTE_negative_improvement() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(-3))
                                		.target(BigDecimal.valueOf(10.300))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }
    
    @Test
    void validate_targets_exist_for_NOVEM_invalid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(5))
                                		.target(BigDecimal.valueOf(100))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
        	.containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS.getMessage());
        verify(validator, times(1))
        	.validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
    }

    @Test
    void validate_throughput_unit_measured_exists_when_sector_NOVEM_invalid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit(null)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .throughputUnit("tonne")                                      
                                        .isTargetUnitThroughputMeasured(Boolean.FALSE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
        	.containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_THROUGHPUT_MEASURED.getMessage());
        verify(validator, times(1))
        	.validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
    }
    
    @Test
    void validate_throughput_unit_when_sector_throughput_unit_exists_and_isTargetUnitThroughputMeasured_is_false() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("tonne")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .throughputUnit("kilos")                                      
                                        .isTargetUnitThroughputMeasured(Boolean.FALSE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
        	.containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_THROUGHPUT_UNIT.getMessage());
        verify(validator, times(1))
        	.validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
    }
    
    @Test
    void validate_throughput_unit_when_sector_NOVEM_and_TU_ABSOLUTE_invalid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit(null)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.target(BigDecimal.valueOf(7.98))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
        	.containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_THROUGHPUT_UNIT.getMessage());
        verify(validator, times(1))
        	.validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
    }
    
    @Test
    void validate_not_valid_target_unit_type() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.CARBON_KG)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_TYPE.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_NOVEM_with_baseline_field_that_should_not_exist() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.TEN)
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .performance(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_RELATIVE_with_no_baseline_field_that_should_exist() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.target(BigDecimal.valueOf(0.079403))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .contains(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_performance_not_valid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .performance(BigDecimal.TEN)
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.target(BigDecimal.valueOf(0.0794030))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_COMPOSITION_PERFORMANCE.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_zero_performance() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.ZERO)
                                        .usedReportingMechanism(Boolean.TRUE)
                                        .throughput(BigDecimal.valueOf(100.50000))
                                        .performance(BigDecimal.ZERO)
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                        .target(BigDecimal.ZERO)
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_RELATIVE_invalid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.valueOf(10.00000))
                                        .throughput(BigDecimal.valueOf(0.00000))
                                        .performance(BigDecimal.ZERO)
                                        .energyCarbonFactor(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                        .improvement(BigDecimal.valueOf(20.2))
                                        .target(BigDecimal.valueOf(0.93))
                                        .build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactlyInAnyOrder(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE.getMessage(),
                        UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void validate_file_type_not_valid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.empty());
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.PDF.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE.getMessage());
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }

    @Test
    void validate_constraints_not_valid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UUID calculatorFile = UUID.randomUUID();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.sectorThroughputUnit("unit")
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod6Details(TargetPeriod6Details.builder()
                                .targetComposition(TargetComposition.builder()
                                        .calculatorFile(calculatorFile)
                                        .measurementType(MeasurementType.ENERGY_GJ)
                                        .agreementCompositionType(AgreementCompositionType.NOVEM)
                                        .build())
                                .baselineData(BaselineData.builder()
                                        .isTwelveMonths(Boolean.FALSE)
                                        .baselineDate(LocalDate.now())
                                        .explanation("My explanation")
                                        .energy(BigDecimal.TEN)
                                        .build())
                                .targets(Targets.builder()
                                		.improvement(BigDecimal.valueOf(20.2))
                                		.build())
                                .build())
                        .build())
                .build();

        when(validator.validate(container.getUnderlyingAgreement().getTargetPeriod6Details()))
                .thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentService.getFileDTO(calculatorFile.toString()))
                .thenReturn(FileDTO.builder().fileType((String) FileType.XLSX.getMimeTypes().toArray()[0]).build());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1))
                .validate(container.getUnderlyingAgreement().getTargetPeriod6Details());
        verify(fileAttachmentService, times(1))
                .getFileDTO(calculatorFile.toString());
    }

    @Test
    void validate_no_data_valid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verifyNoInteractions(validator, fileAttachmentService);
    }
}
