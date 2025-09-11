package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementSubmitInitializerTest {

    @InjectMocks
    private UnderlyingAgreementSubmitInitializer handler;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void initializePayload() {
        final Long accountId = 1L;
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final Request request = Request.builder()
                .id("UNA-ADS_1T00001")
                .metadata(UnderlyingAgreementRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT)
                        .workflowSchemeVersion(workflowSchemeVersion)
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
        final AccountReferenceData data = AccountReferenceData.builder()
                .targetUnitAccountDetails(accountDetails)
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();

        final UnderlyingAgreementSubmitRequestTaskPayload expected =
                UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
                                        .build())
                                .build())
                        .accountReferenceData(data)
                        .build();

        when(accountReferenceDetailsService.getAccountReferenceData(accountId)).thenReturn(data);

        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        verify(accountReferenceDetailsService, times(1)).getAccountReferenceData(accountId);
        assertThat(actual).isInstanceOf(UnderlyingAgreementSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_SUBMIT);
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
