package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.handler;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountCreationService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.transform.TargetUnitAccountPayloadMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType.TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestType.TARGET_UNIT_ACCOUNT_CREATION;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountCreationSubmitApplicationCreateActionHandlerTest {

    @InjectMocks
    private TargetUnitAccountCreationSubmitApplicationCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private TargetUnitAccountPayloadMapper targetUnitAccountPayloadMapper;

    @Mock
    private TargetUnitAccountCreationService targetUnitAccountCreationService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {
        AppUser appUser = AppUser.builder().userId("user").firstName("fn").lastName("ln").build();
        Long accountId = 1L;
        Long sectorAssociationId = 1L;
        String businessId = "businessId";

        TargetUnitAccountPayload accountPayload = TargetUnitAccountPayload.builder()
                .name("account")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .build();

        TargetUnitAccountCreationSubmitApplicationCreateActionPayload createActionPayload = TargetUnitAccountCreationSubmitApplicationCreateActionPayload
                .builder()
                .payloadType(CcaRequestCreateActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD)
                .payload(accountPayload).build();

        TargetUnitAccountCreationRequestPayload requestPayload = TargetUnitAccountCreationRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD)
                .payload(accountPayload)
                .sectorAssociationId(sectorAssociationId)
                .businessId(businessId)
                .build();

        TargetUnitAccountCreationSubmittedRequestActionPayload accountSubmittedPayload = TargetUnitAccountCreationSubmittedRequestActionPayload
                .builder()
                .payloadType(TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED_PAYLOAD)
                .payload(accountPayload)
                .build();

        TargetUnitAccountDTO accountDTO = TargetUnitAccountDTO.builder()
                .name("name")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getAdministrativeContactDetailsDTO())
                .responsiblePerson(getResponsiblePersonDTO())
                .businessId(businessId)
                .build();

        TargetUnitAccountDTO persistedAccountDTO = TargetUnitAccountDTO.builder()
                .id(accountId)
                .name("account")
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .operatorType(TargetUnitAccountOperatorType.PARTNERSHIP)
                .companyRegistrationNumber("companyRegistrationNumber")
                .sicCodes(List.of("sicCode"))
                .sectorAssociationId(sectorAssociationId)
                .address(AccountAddressDTO.builder().build())
                .administrativeContactDetails(getPersistedAdministrativeContactDetailsDTO())
                .responsiblePerson(getPersistedResponsiblePersonDTO())
                .businessId(businessId)
                .build();

        RequestParams requestParams = RequestParams.builder()
                .type(TARGET_UNIT_ACCOUNT_CREATION)
                .requestResources(Map.of(
                		ResourceType.ACCOUNT, accountId.toString(),
                		CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
                		))
                .requestPayload(requestPayload)
                .processVars(Map.of(BpmnProcessConstants.ACCOUNT_ID, accountId))
                .build();

        Request request = Request.builder().creationDate(LocalDateTime.now()).build();
        addCaResourceToRequest(CompetentAuthorityEnum.ENGLAND, request);

        when(targetUnitAccountPayloadMapper.toTargetUnitAccountDTO(accountPayload, sectorAssociationId, appUser.getUserId())).thenReturn(accountDTO);
        when(targetUnitAccountPayloadMapper.toTargetUnitAccountCreationRequestPayload(accountPayload, businessId, sectorAssociationId)).thenReturn(requestPayload);
        when(targetUnitAccountPayloadMapper.toTargetUnitAccountCreationSubmittedRequestActionPayload(accountPayload, businessId)).thenReturn(accountSubmittedPayload);
        when(targetUnitAccountCreationService.createAccount(accountDTO)).thenReturn(persistedAccountDTO);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        // Invoke
        handler.process(sectorAssociationId, null,
                CcaRequestCreateActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD, createActionPayload, appUser);

        // Verify
        assertThat(request.getSubmissionDate()).isEqualTo(request.getCreationDate());
        verify(targetUnitAccountCreationService, times(1)).createAccount(accountDTO);
        verify(targetUnitAccountPayloadMapper, times(1))
                .toTargetUnitAccountDTO(accountPayload, sectorAssociationId, appUser.getUserId());
        verify(targetUnitAccountPayloadMapper, times(1))
                .toTargetUnitAccountCreationRequestPayload(accountPayload, businessId, sectorAssociationId);
        verify(targetUnitAccountPayloadMapper, times(1))
                .toTargetUnitAccountCreationSubmittedRequestActionPayload(accountPayload, businessId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);

        verify(requestService, times(1))
                .addActionToRequest(request,
                        accountSubmittedPayload,
                        TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED,
                        appUser.getUserId());
    }

    @NotNull
    private static TargetUnitAccountContactDTO getAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .address(AccountAddressDTO.builder().build()).email("email").build();
    }

    @NotNull
    private static TargetUnitAccountContactDTO getResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("code").number("number").build()).build();
    }


    @NotNull
    private static TargetUnitAccountContactDTO getPersistedAdministrativeContactDetailsDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .address(AccountAddressDTO.builder().build()).email("email")
                .build();
    }

    @NotNull
    private static TargetUnitAccountContactDTO getPersistedResponsiblePersonDTO() {
        return TargetUnitAccountContactDTO.builder()
                .jobTitle("jobTitle").firstName("firstName").lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("code").number("number").build())
                .address(AccountAddressDTO.builder().build()).email("email")
                .build();
    }
    
    private void addCaResourceToRequest(CompetentAuthorityEnum ca, Request request) {
    	RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.CA)
				.resourceId(ca.name())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
