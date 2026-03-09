package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitInitializerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitInitializer handler;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void initializePayload() {
    	final LocalDateTime creationDate = LocalDateTime.now();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final Long accountId = 1L;
        final String facilityId = "facilityId";
        final UnderlyingAgreementContainer originalData = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder()
                                        .status(FacilityStatus.LIVE)
                                        .facilityItem(FacilityItem.builder()
                                                .facilityId(facilityId)
                                                .facilityDetails(FacilityDetails.builder()
                                                        .name("Fac Name")
                                                        .isCoveredByUkets(false)
                                                        .participatingSchemeVersions(new HashSet<>(Set.of(SchemeVersion.CCA_2)))
                                                        .facilityAddress(FacilityAddressDTO.builder()
                                                                .line1("L1").city("City").country("UK").postcode("PC").build())
                                                        .build())
                                                .build())
                                        .build()))
                        .targetPeriod5Details(TargetPeriod5Details.builder().build())
                        .targetPeriod6Details(TargetPeriod6Details.builder().build())
                        .build())
                .underlyingAgreementAttachments(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id("ADS_53-T00037-VAR-1")
                .creationDate(creationDate)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .originalUnderlyingAgreementContainer(originalData)
                        .build())
                .build();
        addResourcesToRequest(accountId, request);

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

        final AccountReferenceData data = AccountReferenceData.builder()
                .targetUnitAccountDetails(accountDetails)
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();

        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload expected =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
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
                                .underlyingAgreement(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement())
                                .build())
                        .accountReferenceData(data)
                        .originalUnderlyingAgreementContainer(originalData)
                        .regulatorLedSubmitAttachments(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreementAttachments())
                        .build();

        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(data);
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowTp5Tp6(originalData, creationDate.toLocalDate()))
        		.thenReturn(true);

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(accountId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowTp5Tp6(originalData, creationDate.toLocalDate());
        assertThat(actual).isInstanceOf(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void initializePayload_hidesTp5Tp6_whenOnlyCca3Live() {
    	final LocalDateTime creationDate = LocalDateTime.now();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final Long accountId = 2L;
        final String facilityId = "facilityId2";
        final UnderlyingAgreementContainer originalData = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(
                                Facility.builder()
                                        .status(FacilityStatus.LIVE)
                                        .facilityItem(FacilityItem.builder()
                                                .facilityId(facilityId)
                                                .facilityDetails(FacilityDetails.builder()
                                                        .name("Fac Name 2")
                                                        .isCoveredByUkets(false)
                                                        .participatingSchemeVersions(new HashSet<>(Set.of(SchemeVersion.CCA_3)))
                                                        .facilityAddress(FacilityAddressDTO.builder()
                                                                .line1("L1").city("City").country("UK").postcode("PC").build())
                                                        .build())
                                                .build())
                                        .build()))
                        .build())
                .underlyingAgreementAttachments(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id("ADS_53-T00037-VAR-2")
                .creationDate(creationDate)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .originalUnderlyingAgreementContainer(originalData)
                        .build())
                .build();
        addResourcesToRequest(accountId, request);

        final TargetUnitAccountContactDTO responsiblePerson = TargetUnitAccountContactDTO.builder()
                .email("yy@test.gr")
                .firstName("First2")
                .lastName("Last2")
                .jobTitle("Job2")
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
                .operatorName("Operator Name 2")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .companyRegistrationNumber("22222")
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
                .subsectorAssociationName("SUBSECTOR_2")
                .schemeDataMap(Map.of(SchemeVersion.CCA_3, SchemeData.builder()
                        .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                        .build()))
                .build();

        final AccountReferenceData data = AccountReferenceData.builder()
                .targetUnitAccountDetails(accountDetails)
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();

        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload expected =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
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
                                .underlyingAgreement(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement())
                                .build())
                        .accountReferenceData(data)
                        .originalUnderlyingAgreementContainer(originalData)
                        .regulatorLedSubmitAttachments(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreementAttachments())
                        .build();

        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(data);
        when(underlyingAgreementSchemeVersionsHelperService.shouldShowTp5Tp6(originalData, creationDate.toLocalDate()))
        		.thenReturn(false);

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(accountId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).shouldShowTp5Tp6(originalData, creationDate.toLocalDate());
        assertThat(actual).isInstanceOf(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void initializePayload_from_peer_review() {
        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();
        final UnderlyingAgreementVariationPayload underlyingAgreement = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder().status(FacilityStatus.LIVE).build()))
                        .build())
                .build();
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();
        final Map<String, LocalDate> facilityChargeStartDateMap = Map.of("facility", LocalDate.now());
        final Map<UUID, String> underlyingAgreementAttachments = Map.of(UUID.randomUUID(), "attachment");
        final Map<UUID, String> regulatorLedSubmitAttachments = Map.of(UUID.randomUUID(), "reg-led-attachment");
        final Map<String, String> sectionsCompleted = Map.of("subtask", "COMPLETED");
        final Request request = Request.builder()
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreementProposed(underlyingAgreement)
                        .regulatorLedDetermination(determination)
                        .regulatorLedFacilityChargeStartDateMap(facilityChargeStartDateMap)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .build();

        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload expected =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PAYLOAD)
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .determination(determination)
                        .facilityChargeStartDateMap(facilityChargeStartDateMap)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .regulatorLedSubmitAttachments(regulatorLedSubmitAttachments)
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        verifyNoInteractions(accountReferenceDetailsService);
        verifyNoInteractions(underlyingAgreementSchemeVersionsHelperService);
        assertThat(actual).isInstanceOf(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT);
    }


    private void addResourcesToRequest(Long accountId, Request request) {
        RequestResource accountResource = RequestResource.builder()
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(accountResource);
    }
}
