package uk.gov.cca.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailableRequestService {

    private final AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;
    private final List<RequestCreateByAccountValidator> requestCreateByAccountValidators;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    @Transactional
    public Map<RequestCreateActionType, RequestCreateValidationResult> getAvailableAccountWorkflows(final Long accountId,
                                                                                                    final AppUser appUser) {

        Set<RequestType> availableCreateRequestTypes = RequestType.getAvailableForAccountCreateRequestTypes();
        Set<RequestCreateActionType> actions = getAvailableCreateActions(accountId, appUser, availableCreateRequestTypes);

        return actions.stream()
                .collect(Collectors.toMap(
                        requestType -> requestType,
                        requestType -> getAccountValidationResult(requestType, accountId)))
                .entrySet().stream()
                .filter(a -> a.getValue().isAvailable())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<RequestCreateActionType> getAvailableCreateActions(final Long accountId,
                                                                   final AppUser appUser,
                                                                   final Set<RequestType> availableCreateRequestTypes) {
        return accountRequestAuthorizationResourceService
                .findRequestCreateActionsByAccountId(appUser, accountId).stream()
                .map(RequestCreateActionType::valueOf)
                .filter(type -> enabledWorkflowValidator.isWorkflowEnabled(type.getType()))
                .filter(type -> availableCreateRequestTypes.contains(type.getType()))
                .collect(Collectors.toSet());
    }

    private RequestCreateValidationResult getAccountValidationResult(RequestCreateActionType type, long accountId) {
        return requestCreateByAccountValidators.stream()
                .filter(validator -> validator.getType().equals(type))
                .findFirst()
                .map(validator -> validator.validateAction(accountId))
                .orElse(RequestCreateValidationResult.builder().valid(true).build());
    }
}
