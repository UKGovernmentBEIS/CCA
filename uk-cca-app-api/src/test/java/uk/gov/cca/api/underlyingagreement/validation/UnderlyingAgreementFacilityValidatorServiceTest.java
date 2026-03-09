package uk.gov.cca.api.underlyingagreement.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_TARGETS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_UNIQUE_PRODUCT_NAME;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityValidationContext;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.config.UnderlyingAgreementConfig;
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

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementFacilityValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementFacilityValidatorService validatorService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private UnderlyingAgreementConfig underlyingAgreementConfig;

    @Test
    void validate() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        		
        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds))
        		.thenReturn(new HashMap<>());

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_LIVE_with_CCA2_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.LIVE, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityId(), FacilityValidationContext
				.builder().facilityBusinessId(facility.getFacilityItem().getFacilityId())
				.closedDate(null)
				.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_exists_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityId(), FacilityValidationContext
				.builder().facilityBusinessId(facility.getFacilityItem().getFacilityId())
				.closedDate(null)
				.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);
        
        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_LIVE_not_exists_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.LIVE, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityId(),
				FacilityValidationContext.builder().facilityBusinessId(facility.getFacilityItem().getFacilityId())
						.closedDate(LocalDate.of(2022, 01, 01))
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_previous_facility_not_active_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId(),
				FacilityValidationContext.builder()
						.facilityBusinessId(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
						.closedDate(LocalDate.of(2022, 01, 01))
						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.now().minusDays(1));

        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_PREVIOUS_FACILITY_ID.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_measurement_type_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.CARBON_KG).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_TARGET_UNIT_TYPE.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_not_all_improvements_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityTargets().setImprovements(Map.of(
                TargetImprovementType.TP8, BigDecimal.TEN,
                TargetImprovementType.TP9, BigDecimal.TEN
        ));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(INVALID_FACILITY_TARGETS.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_NEW_with_only_CCA2_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_previous_facility_CCA3_for_both_schema_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId(),
				FacilityValidationContext.builder()
						.facilityBusinessId(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
						.closedDate(null)
						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.now().minusDays(1));
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .containsExactly(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_CCA2_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_2;
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, null, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2));
        facility.getFacilityItem().setCca3BaselineAndTargets(null);
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).isEmpty();
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_facility_scheme_greater_than_current_scheme_version_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_2;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage(),
                INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validate_participating_scheme_versions_on_change_of_ownership_after_cutoff_date_not_equal_should_be_invalid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";

        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP);
        facility.getFacilityItem().getFacilityDetails().setPreviousFacilityId(previousFacilityId);
        facility.getFacilityItem().getFacilityDetails().setParticipatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));

        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2027, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);
        Map<String, FacilityValidationContext> facilityValidationContextMap = new HashMap<>();
		facilityValidationContextMap.put(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId(),
				FacilityValidationContext.builder()
						.facilityBusinessId(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
						.closedDate(null)
						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
						.build());

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(facilityValidationContextMap);
        when(underlyingAgreementConfig.getSchemeParticipationFlagCutOffDate()).thenReturn(LocalDate.of(2025, 1, 1));

        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage)
                .contains(INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE.getMessage());

        verify(facilityDataQueryService, times(1)).getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validateFacilityBaselineEnergyConsumption_by_product_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        // exclude all products
        facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                .forEach(p -> p.setProductStatus(ProductStatus.EXCLUDED));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        
        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactlyInAnyOrder(
                INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS.getMessage(),
                INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validateFacilityBaselineEnergyConsumption_by_product_status_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        // exclude all products
        facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption().getVariableEnergyConsumptionDataByProduct()
                .forEach(p -> p.setProductStatus(ProductStatus.LIVE));
        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    @Test
    void validateFacilityBaselineEnergyConsumption_by_product_duplicate_names_not_valid() {
        final SchemeVersion currentSchemeVersion = SchemeVersion.CCA_3;
        final UUID evidenceFile = UUID.randomUUID();
        final UUID calculatorFile = UUID.randomUUID();
        final Facility facility = createFacility(FacilityStatus.NEW, evidenceFile, calculatorFile, Set.of(SchemeVersion.CCA_3));
        List<ProductVariableEnergyConsumptionData> products = List.of(
                ProductVariableEnergyConsumptionData.builder()
                        .baselineYear(Year.of(2022))
                        .productName("FacilityProduct1")
                        .energy(BigDecimal.valueOf(-125.69))
                        .throughput(BigDecimal.valueOf(56))
                        .throughputUnit("Each")
                        .productStatus(ProductStatus.NEW)
                        .build(),
                ProductVariableEnergyConsumptionData.builder()
                        .baselineYear(Year.of(2022))
                        .productName("FacilityProduct2")
                        .energy(BigDecimal.valueOf(-125.69))
                        .throughput(BigDecimal.valueOf(56))
                        .throughputUnit("Each")
                        .productStatus(ProductStatus.NEW)
                        .build(),
                ProductVariableEnergyConsumptionData.builder()
                        .baselineYear(Year.of(2022))
                        .productName("FacilityProduct2")
                        .energy(BigDecimal.valueOf(-125.69))
                        .throughput(BigDecimal.valueOf(56))
                        .throughputUnit("Each")
                        .productStatus(ProductStatus.NEW)
                        .build()
                );
        facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityBaselineEnergyConsumption()
                .setVariableEnergyConsumptionDataByProduct(products);

        final UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .schemeVersion(currentSchemeVersion)
                .requestCreationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder().sectorMeasurementType(MeasurementType.ENERGY_KWH).build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility))
                        .build())
                .build();

        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        Set<String> facilityBusinessIds = getFacilityAndPreviousFacilityBusinessIds(facility);

        when(facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds)).thenReturn(new HashMap<>());
        
        // Invoke
        validatorService.validate(facility, container, underlyingAgreementValidationContext, violations);

        // Verify
        assertThat(violations).extracting(UnderlyingAgreementViolation::getMessage).containsExactly(
                INVALID_UNIQUE_PRODUCT_NAME.getMessage());
        verify(facilityDataQueryService, times(1))
                .getFacilityValidationContextByFacilityBusinessIds(facilityBusinessIds);
        verifyNoMoreInteractions(facilityDataQueryService);
    }

    private Facility createFacility(FacilityStatus status, UUID evidenceFile, UUID calculatorFile, Set<SchemeVersion> schemeVersions) {
        return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(UUID.randomUUID().toString())
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.NEW_AGREEMENT)
                                .participatingSchemeVersions(schemeVersions)
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
    


	private Set<String> getFacilityAndPreviousFacilityBusinessIds(final Facility facility) {
		Set<String> facilityBusinessIds = new HashSet<>();
		facilityBusinessIds.add(facility.getFacilityItem().getFacilityId());
		if (facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId() != null) {
			facilityBusinessIds.add(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId());
		}
		return facilityBusinessIds;
	}
}
