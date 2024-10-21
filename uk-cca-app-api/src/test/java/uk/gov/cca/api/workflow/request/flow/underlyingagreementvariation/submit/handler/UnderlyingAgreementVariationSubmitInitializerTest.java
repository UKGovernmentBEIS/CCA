package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSubmitInitializerTest {

    @InjectMocks
    private UnderlyingAgreementVariationSubmitInitializer handler;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void initializePayload() {
        final Long accountId = 1L;
        final Request request = Request.builder()
                .id("ADS_53-T00037-VAR-1")
                .accountId(accountId)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .underlyingAgreementAttachments(new HashMap<>())
                                .build())
                        .build())
                .build();

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
                .measurementType(MeasurementType.ENERGY_KWH)
                .build();

        final AccountReferenceData data = AccountReferenceData.builder()
                .targetUnitAccountDetails(accountDetails)
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();

        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        final UnderlyingAgreementVariationSubmitRequestTaskPayload expected =
                UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
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
                                .underlyingAgreement(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreement())
                                .build())
                        .accountReferenceData(data)
                        .originalUnderlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .underlyingAgreementAttachments(new HashMap<>())
                                .build())
                        .underlyingAgreementAttachments(requestPayload.getOriginalUnderlyingAgreementContainer().getUnderlyingAgreementAttachments())
                        .build();

        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(data);

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(accountId);
        assertThat(actual).isInstanceOf(UnderlyingAgreementVariationSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT);
    }


}
