package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FacilityItemTest {

    private Validator validator;

    @Mock
    private FacilityDetails facilityDetails;

    @Mock
    private TargetUnitAccountContactDTO facilityContact;

    @Mock
    private EligibilityDetailsAndAuthorisation eligibilityDetailsAndAuthorisation;

    @Mock
    private FacilityExtent facilityExtent;

    @Mock
    private Apply70Rule apply70Rule;

    @InjectMocks
    private FacilityItem facilityItem;

    private UUID permitFile;

    private UUID processFlowFile;

    private UUID manufacturingProcessFile;

    private UUID annotatedSitePlansFile;

    private UUID eligibleProcessFile;

    private UUID activitiesDescriptionFile;

    private UUID evidenceFile;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        MockitoAnnotations.openMocks(this);

        // Example UUIDs
        permitFile = UUID.randomUUID();
        processFlowFile = UUID.randomUUID();
        manufacturingProcessFile = UUID.randomUUID();
        annotatedSitePlansFile = UUID.randomUUID();
        eligibleProcessFile = UUID.randomUUID();
        activitiesDescriptionFile = UUID.randomUUID();
        evidenceFile = UUID.randomUUID();
    }

    @Test
    void validate_CCA3_no_baselineAndTargets_not_valid() {
        FacilityItem item = FacilityItem.builder()
                .facilityId("ADS_1-F00001")
                .facilityDetails(FacilityDetails.builder()
                        .name("Facility name")
                        .isCoveredByUkets(Boolean.TRUE)
                        .uketsId("uketsId")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .previousFacilityId("AAA_1-F11111")
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
                        .email("responsiblePerson@test.com")
                        .firstName("firstName")
                        .lastName("lastName")
                        .jobTitle("jobTitle")
                        .address(AccountAddressDTO.builder()
                                .line1("line1")
                                .line2("line2")
                                .city("city")
                                .country("country")
                                .postcode("postcode")
                                .build())
                        .build())
                .eligibilityDetailsAndAuthorisation(EligibilityDetailsAndAuthorisation.builder()
                        .isConnectedToExistingFacility(Boolean.TRUE)
                        .adjacentFacilityId("AAA_1-F11111")
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
                        .evidenceFile(UUID.randomUUID())
                        .build())
                .build();

        final Set<ConstraintViolation<FacilityItem>> violations = validator.validate(item);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{underlyingagreement.facilities.cca3BaselineAndTargets}");
    }

    @Test
    void validate_CCA2_with_baselineAndTargets_not_valid() {
        FacilityItem item = FacilityItem.builder()
                .facilityId("ADS_1-F00001")
                .facilityDetails(FacilityDetails.builder()
                        .name("Facility name")
                        .isCoveredByUkets(Boolean.TRUE)
                        .uketsId("uketsId")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .previousFacilityId("AAA_1-F11111")
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
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
                        .email("responsiblePerson@test.com")
                        .firstName("firstName")
                        .lastName("lastName")
                        .jobTitle("jobTitle")
                        .address(AccountAddressDTO.builder()
                                .line1("line1")
                                .line2("line2")
                                .city("city")
                                .country("country")
                                .postcode("postcode")
                                .build())
                        .build())
                .eligibilityDetailsAndAuthorisation(EligibilityDetailsAndAuthorisation.builder()
                        .isConnectedToExistingFacility(Boolean.TRUE)
                        .adjacentFacilityId("AAA_1-F11111")
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
                        .evidenceFile(UUID.randomUUID())
                        .build())
                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder().build())
                .build();

        final Set<ConstraintViolation<FacilityItem>> violations = validator.validate(item);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("{underlyingagreement.facilities.cca3BaselineAndTargets}");
    }

    @Test
    void whenFacilityIdIsValid_thenNoValidationErrors() {
        // Valid facilityId
        facilityItem = FacilityItem.builder()
                .facilityId("ADS_1-F00001")
                .facilityDetails(facilityDetails)
                .facilityContact(facilityContact)
                .eligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation)
                .facilityExtent(facilityExtent)
                .apply70Rule(apply70Rule)
                .build();

        Set<ConstraintViolation<FacilityItem>> violations = validator.validate(facilityItem);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .doesNotContain("underlyingagreement.facilities.facilityId");
    }

    @Test
    void whenFacilityIdIsInvalid_without_F() {

        facilityItem = FacilityItem.builder()
                .facilityId("ADS_1-000001")  // Invalid ID
                .facilityDetails(facilityDetails)
                .facilityContact(facilityContact)
                .eligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation)
                .facilityExtent(facilityExtent)
                .apply70Rule(apply70Rule)
                .build();

        Set<ConstraintViolation<FacilityItem>> violations = validator.validate(facilityItem);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("underlyingagreement.facilities.facilityId");
    }

    @Test
    void whenFacilityIdIsInvalid_with_4_digits() {

        facilityItem = FacilityItem.builder()
                .facilityId("ADS_1-F0001")  // Invalid ID
                .facilityDetails(facilityDetails)
                .facilityContact(facilityContact)
                .eligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation)
                .facilityExtent(facilityExtent)
                .apply70Rule(apply70Rule)
                .build();

        Set<ConstraintViolation<FacilityItem>> violations = validator.validate(facilityItem);

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("underlyingagreement.facilities.facilityId");
    }


    @Test
    void whenAllFilesExist_thenAllUUIDsAreReturned() {
        // Mock the return values of the files
        when(eligibilityDetailsAndAuthorisation.getPermitFile()).thenReturn(permitFile);
        when(facilityExtent.getProcessFlowFile()).thenReturn(processFlowFile);
        when(facilityExtent.getManufacturingProcessFile()).thenReturn(manufacturingProcessFile);
        when(facilityExtent.getAnnotatedSitePlansFile()).thenReturn(annotatedSitePlansFile);
        when(facilityExtent.getEligibleProcessFile()).thenReturn(eligibleProcessFile);
        when(facilityExtent.getActivitiesDescriptionFile()).thenReturn(activitiesDescriptionFile);
        when(apply70Rule.getEvidenceFile()).thenReturn(evidenceFile);

        // Set the mocked objects to the facilityItem
        facilityItem.setEligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation);
        facilityItem.setFacilityExtent(facilityExtent);
        facilityItem.setApply70Rule(apply70Rule);

        // Execute the method
        Set<UUID> attachments = facilityItem.getAttachmentIds();

        // Assert that all files are present in the attachments set
        assertEquals(7, attachments.size());
        assertEquals(Set.of(permitFile, processFlowFile, manufacturingProcessFile, annotatedSitePlansFile, eligibleProcessFile, activitiesDescriptionFile, evidenceFile), attachments);
    }

    @Test
    void whenSomeFilesAreNull_thenOnlyNonNullUUIDsAreReturned() {
        // Mock the return values, some being null
        when(eligibilityDetailsAndAuthorisation.getPermitFile()).thenReturn(permitFile);
        when(facilityExtent.getProcessFlowFile()).thenReturn(processFlowFile);
        when(facilityExtent.getManufacturingProcessFile()).thenReturn(null);
        when(facilityExtent.getAnnotatedSitePlansFile()).thenReturn(annotatedSitePlansFile);
        when(facilityExtent.getEligibleProcessFile()).thenReturn(null);
        when(facilityExtent.getActivitiesDescriptionFile()).thenReturn(activitiesDescriptionFile);
        when(apply70Rule.getEvidenceFile()).thenReturn(null);

        // Set the mocked objects to the facilityItem
        facilityItem.setEligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation);
        facilityItem.setFacilityExtent(facilityExtent);
        facilityItem.setApply70Rule(apply70Rule);

        // Execute the method
        Set<UUID> attachments = facilityItem.getAttachmentIds();

        // Assert that only the non-null files are present
        assertEquals(4, attachments.size());
        assertEquals(Set.of(permitFile, processFlowFile, annotatedSitePlansFile, activitiesDescriptionFile), attachments);
    }

    @Test
    void whenNoFilesExist_thenEmptySetIsReturned() {
        // Mock all return values to be null
        when(eligibilityDetailsAndAuthorisation.getPermitFile()).thenReturn(null);
        when(facilityExtent.getProcessFlowFile()).thenReturn(null);
        when(facilityExtent.getManufacturingProcessFile()).thenReturn(null);
        when(facilityExtent.getAnnotatedSitePlansFile()).thenReturn(null);
        when(facilityExtent.getEligibleProcessFile()).thenReturn(null);
        when(facilityExtent.getActivitiesDescriptionFile()).thenReturn(null);
        when(apply70Rule.getEvidenceFile()).thenReturn(null);

        // Set the mocked objects to the facilityItem
        facilityItem.setEligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation);
        facilityItem.setFacilityExtent(facilityExtent);
        facilityItem.setApply70Rule(apply70Rule);

        // Execute the method
        Set<UUID> attachments = facilityItem.getAttachmentIds();

        // Assert that the set is empty
        assertEquals(0, attachments.size());
    }

}