package uk.gov.cca.api.workflow.request.flow.cca2termination.common.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
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

@Validated
@Service
@RequiredArgsConstructor
public class Cca2TerminationRunInitiateService {

	private final Cca2TerminationCreateValidator cca2TerminationCreateValidator;
    private final StartProcessRequestService startProcessRequestService;
    private final FacilityDataQueryService facilityDataQueryService;
	private final Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

	public boolean isValidForCca2TerminationRun() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.equals(cca2TerminationWorkflowConfig.getTriggerDate());
    }
	
	public void createCca2TerminationRun(List<String> accountBusinessIds) {
		// Validate
        validateCreation();

        Map<Long, Cca2TerminationAccountState> cca2TerminationAccountStates = facilityDataQueryService
        		.findLiveAccountsWithAtLeastOneFacilityForSchemeVersionOnly(SchemeVersion.CCA_2.name()).stream()
        		.filter(ObjectUtils.isEmpty(accountBusinessIds) ? acc -> true : acc -> accountBusinessIds.contains(acc.getBusinessId()))
        		.collect(Collectors.toMap(
        				TargetUnitAccountBusinessInfoDTO::getAccountId,
                        state -> Cca2TerminationAccountState.builder()
                                .accountId(state.getAccountId())
                                .accountBusinessId(state.getBusinessId())
                                .build()
                ));

        // Start process
        startCca2TerminationRun(cca2TerminationAccountStates);
		
	}
	
	private void validateCreation() {
        final RequestCreateValidationResult validationResult = cca2TerminationCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());

        if(!validationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.CCA2_TERMINATION_RUN_EXIST);
        }
    }

    private void startCca2TerminationRun(Map<Long, Cca2TerminationAccountState> cca2TerminationAccountStates) {
        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA2_TERMINATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestMetadata(Cca2TerminationRunRequestMetadata.builder()
                		.type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
						.cca2TerminationAccountStates(cca2TerminationAccountStates)
						.build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(cca2TerminationAccountStates.keySet()),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        // Start process
        Request request = startProcessRequestService.startProcess(requestParams);
        
        // Set submission date
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
    }
}
