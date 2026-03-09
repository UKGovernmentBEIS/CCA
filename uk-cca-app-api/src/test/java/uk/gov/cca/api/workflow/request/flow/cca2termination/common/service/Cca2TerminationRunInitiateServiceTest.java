package uk.gov.cca.api.workflow.request.flow.cca2termination.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.config.Cca2TerminationWorkflowConfig;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.validation.Cca2TerminationCreateValidator;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunInitiateServiceTest {

	@InjectMocks
    private Cca2TerminationRunInitiateService cca2TerminationRunInitiateService;

    @Mock
    private Cca2TerminationCreateValidator validator;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void isValidForCca2TerminationRun() {
    	
        when(cca2TerminationWorkflowConfig.getTriggerDate()).thenReturn(LocalDate.now().plusDays(1));

        // Invoke
        boolean result = cca2TerminationRunInitiateService.isValidForCca2TerminationRun();

        // Verify
        assertThat(result).isFalse();
        verify(cca2TerminationWorkflowConfig, times(1)).getTriggerDate();
    }

    @Test
    void createCca2TerminationRun() {
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();
        final Request request = Request.builder().build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA2_TERMINATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestMetadata(Cca2TerminationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                        .cca2TerminationAccountStates(Map.of(1L, Cca2TerminationAccountState.builder()
                        		.accountId(1L)
                        		.accountBusinessId("BUSINESS_1")
                        		.build()))
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
				TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("BUSINESS_1").build()
		);

        when(validator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(facilityDataQueryService.findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly("CCA_2"))
        		.thenReturn(accounts);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        // Invoke
        cca2TerminationRunInitiateService.createCca2TerminationRun(List.of());

        // Verify
        verify(validator, times(1)).validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verify(facilityDataQueryService, times(1)).findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly("CCA_2");
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void createCca2TerminationRun_in_progress() {
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();

        when(validator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cca2TerminationRunInitiateService.createCca2TerminationRun(List.of()));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA2_TERMINATION_RUN_EXIST);
        verify(validator, times(1)).validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verifyNoInteractions(facilityDataQueryService);
        verifyNoInteractions(startProcessRequestService);
    }
}
