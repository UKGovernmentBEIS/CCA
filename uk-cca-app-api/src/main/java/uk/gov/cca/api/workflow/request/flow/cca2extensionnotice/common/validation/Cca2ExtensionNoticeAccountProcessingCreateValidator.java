package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingCreateValidator implements RequestCreateByAccountValidator {

    private final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
        final boolean exist = requestQueryService.existsRequestByAccountAndType(
                accountId, this.getRequestType());

        if(exist) {
           // CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING should not exist
            return RequestCreateValidationResult.builder()
                    .valid(false)
                    .reportedRequestTypes(Set.of(this.getRequestType()))
                    .build();
        }
        else {
            // UNDERLYING_AGREEMENT_VARIATION and ADMIN_TERMINATION should not be in progress
            List<Request> inProgressRequests = this.requestQueryService.findInProgressRequestsByAccount(accountId);
            Set<String> conflictingRequests = inProgressRequests.stream().map(Request::getType).map(RequestType::getCode)
                    .filter(r -> this.getMutuallyExclusiveRequests().contains(r)).collect(Collectors.toSet());
            if (!conflictingRequests.isEmpty()) {
                return RequestCreateValidationResult.builder()
                        .valid(false)
                        .reportedRequestTypes(conflictingRequests)
                        .build();
            }

            return RequestCreateValidationResult.builder().valid(true).build();
        }
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING;
    }

    private Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION, CcaRequestType.ADMIN_TERMINATION);
    }
}
