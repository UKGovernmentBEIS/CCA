package uk.gov.cca.api.workflow.request.core.assignment.requestassign;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class RequestReleaseService {

    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    
    @Transactional
    public void releaseRequest(RequestTask requestTask) {
        if (RequestTaskType.getSupportingRequestTaskTypes().contains(requestTask.getType())) {
            return;
        }
        Request request = requestTask.getRequest();
        String requestTaskAssignee = requestTask.getAssignee();

        RoleType requestTaskRoleType = authorizationRulesQueryService
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name())
            .orElse(null);

        if (requestTaskRoleType != null) {
            switch (requestTaskRoleType) {
                case OPERATOR:
                    releaseRequestFromOperatorAssignee(request, requestTaskAssignee);
                    break;
                case REGULATOR:
                    releaseRequestFromRegulatorAssignee(request, requestTaskAssignee);
                    break;
                case VERIFIER:
                    //TODO: implement this one later
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("User with role type %s not related with request assignment", requestTaskRoleType));
            }
        }
    }

    private void releaseRequestFromOperatorAssignee(Request request, String taskAssignee) {
        RequestPayload requestPayload = request.getPayload();
        String requestOperatorAssignee = requestPayload.getOperatorAssignee();
        if (!StringUtils.isEmpty(requestOperatorAssignee) &&
            (requestOperatorAssignee.equals(taskAssignee) || StringUtils.isEmpty(taskAssignee))) {
            requestPayload.setOperatorAssignee(null);
        }
    }

    private void releaseRequestFromRegulatorAssignee(Request request, String taskAssignee) {
        RequestPayload requestPayload = request.getPayload();
        String requestRegulatorAssignee = requestPayload.getRegulatorAssignee();
        if (!StringUtils.isEmpty(requestRegulatorAssignee) &&
            (requestRegulatorAssignee.equals(taskAssignee) || StringUtils.isEmpty(taskAssignee))) {
            requestPayload.setRegulatorAssignee(null);
        }
    }
}
