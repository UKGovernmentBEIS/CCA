package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
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
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationApplicationReasonDataValidatorTest {


    @InjectMocks
    private EditedUnderlyingAgreementVariationApplicationReasonDataValidator validatorService;

    @Test
    void validate() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();
        final String previousFacilityId = "previousFacilityId";

        final Facility facility = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile, previousFacilityId);
        final UnderlyingAgreementVariationRequestTaskPayload taskPayload = getUnderlyingAgreementVariationRequestTaskPayload(facility, facility);

        BusinessValidationResult result = validatorService.validate(taskPayload);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_not_valid() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();

        final String previousFacilityId = "previousFacilityId";

        final Facility facility = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile, previousFacilityId);
        final Facility originalFacility = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile, previousFacilityId);
        facility.getFacilityItem().getFacilityDetails().setApplicationReason(ApplicationReasonType.NEW_AGREEMENT);
        facility.getFacilityItem().setFacilityId(originalFacility.getFacilityItem().getFacilityId());

        final UnderlyingAgreementVariationRequestTaskPayload taskPayload = getUnderlyingAgreementVariationRequestTaskPayload(facility, originalFacility);

        BusinessValidationResult result = validatorService.validate(taskPayload);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_throw_exception() {
        final UUID permitFile = UUID.randomUUID();
        final UUID evidenceFile = UUID.randomUUID();

        final String previousFacilityId = "previousFacilityId";
        final String otherPreviousFacilityId = "otherPreviousFacilityId";

        final Facility facility = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile, previousFacilityId);
        final Facility originalFacility = createFacility(FacilityStatus.LIVE, permitFile, evidenceFile, otherPreviousFacilityId);
        originalFacility.getFacilityItem().getFacilityDetails().setPreviousFacilityId("code");

        final UnderlyingAgreementVariationRequestTaskPayload taskPayload = getUnderlyingAgreementVariationRequestTaskPayload(facility, originalFacility);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(taskPayload));

        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
    }

    private static UnderlyingAgreementVariationRequestTaskPayload getUnderlyingAgreementVariationRequestTaskPayload(Facility facility, Facility originalFacility) {
        final TargetUnitAccountContactDTO responsiblePerson = TargetUnitAccountContactDTO.builder()
                .email("xx@test.gr")
                .firstName("First")
                .lastName("Last")
                .jobTitle("Job")
                .address(AccountAddressDTO.builder()
                        .line1("Line 11")
                        .line2("Line 22")
                        .city("City1")
                        .county("County1")
                        .postcode("code1")
                        .country("Country1")
                        .build())
                .build();

        final TargetUnitAccountDetails accountDetails = TargetUnitAccountDetails.builder()
                .operatorName("Operator Name")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .companyRegistrationNumber("11111")
                .address(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
                .responsiblePerson(responsiblePerson)
                .build();

        final SectorAssociationDetails sectorAssociationDetails = SectorAssociationDetails.builder()
                .subsectorAssociationName("SUBSECTOR_1")
                .schemeDataMap(Map.of(SchemeVersion.CCA_2, SchemeData.builder()
        				.sectorMeasurementType(MeasurementType.ENERGY_KWH)
        				.build()))
                .build();

        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .targetUnitAccountDetails(accountDetails)
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();

        final UnderlyingAgreementContainer originalUnderlyingAgreementContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility)).build())
                .underlyingAgreementAttachments(new HashMap<>())
                .build();


        return UnderlyingAgreementVariationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PAYLOAD)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder()
                                        .operatorName(accountDetails.getOperatorName())
                                        .operatorType(accountDetails.getOperatorType())
                                        .isCompanyRegistrationNumber(true)
                                        .companyRegistrationNumber(accountDetails.getCompanyRegistrationNumber())
                                        .operatorAddress(accountDetails.getAddress())
                                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder()
                                                .firstName(responsiblePerson.getFirstName())
                                                .lastName(responsiblePerson.getLastName())
                                                .email(responsiblePerson.getEmail())
                                                .address(responsiblePerson.getAddress())
                                                .build())
                                        .subsectorAssociationName(sectorAssociationDetails.getSubsectorAssociationName())
                                        .build())
                                .underlyingAgreement(originalUnderlyingAgreementContainer.getUnderlyingAgreement())
                                .build())
                        .accountReferenceData(accountReferenceData)
                        .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(originalFacility)).build())
                                .underlyingAgreementAttachments(new HashMap<>())
                                .build())
                        .underlyingAgreementAttachments(originalUnderlyingAgreementContainer.getUnderlyingAgreementAttachments())
                        .build();
    }

    private Facility createFacility(FacilityStatus status, UUID permitFile, UUID evidenceFile, String previousFacilityId) {
        return Facility.builder()
                .status(status)
                .facilityItem(FacilityItem.builder()
                        .facilityId(UUID.randomUUID().toString())
                        .facilityDetails(FacilityDetails.builder()
                                .isCoveredByUkets(Boolean.TRUE)
                                .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                                .previousFacilityId(previousFacilityId)
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
