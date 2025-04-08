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
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;

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
    public void setUp() {
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