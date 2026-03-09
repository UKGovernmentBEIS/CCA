package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSubmitFacilitiesContextValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService validatorService;

    @Test
    void validate() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.EXCLUDED, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.NEW, permitFile, evidenceFile);
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_not_valid() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final Facility facility1 = createFacility(FacilityStatus.EXCLUDED, permitFile, evidenceFile);
        final Facility facility2 = createFacility(FacilityStatus.EXCLUDED, permitFile, evidenceFile);
        facility2.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(null);

        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
        		.schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(facility1, facility2))
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container);

        // Verify
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void getSectionName() {
        assertThat(validatorService.getSectionName()).isEqualTo(Facility.class.getName());
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
