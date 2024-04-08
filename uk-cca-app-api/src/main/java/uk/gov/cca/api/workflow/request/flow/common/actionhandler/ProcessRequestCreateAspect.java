package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.account.service.AccountQueryService;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByCAValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.cca.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
public class ProcessRequestCreateAspect {

    private final List<RequestCreateByAccountValidator> requestCreateByAccountValidators;

    private final List<RequestCreateByCAValidator> requestCreateByCAValidators;
    private final AccountQueryService accountQueryService;
    private final CompetentAuthorityService competentAuthorityService;

    @Before("execution(* uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler.process*(..))")
    public void process(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        final Long accountId = (Long) args[0];
        final RequestCreateActionType type = (RequestCreateActionType)args[1];
        final RequestCreateActionPayload payload = (RequestCreateActionPayload)args[2];
        final AppUser currentUser = (AppUser)args[3];

        if (accountId != null) {
            Optional<RequestCreateByAccountValidator> requestCreateByAccountValidatorOpt = requestCreateByAccountValidators
                    .stream().filter(requestCreateValidator -> requestCreateValidator.getType() == type).findFirst();

            if (requestCreateByAccountValidatorOpt.isEmpty()) {
                return;
            }

            if (accountId != null) {
                Set<RequestType> allAvailableRequests = new HashSet<>(RequestType.getAvailableForAccountCreateRequestTypes());

                if (!allAvailableRequests.contains(type.getType())) {
                    throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED,
                            String.format("%s is not supported", type));
                }
                // lock the account
                accountQueryService.exclusiveLockAccount(accountId);
            }

            final RequestCreateValidationResult validationResult = requestCreateByAccountValidatorOpt
                    .map(requestCreateByAccountValidator -> requestCreateByAccountValidator.validateAction(accountId))
                    .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build());

            if (!validationResult.isValid() || !validationResult.isAvailable()) {
                throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResult);
            }
        } else {
            Optional<RequestCreateByCAValidator> requestCreateByCAValidatorOpt = requestCreateByCAValidators
                    .stream().filter(requestCreateValidator -> requestCreateValidator.getType() == type).findFirst();
            if (requestCreateByCAValidatorOpt.isEmpty()) {
                return;
            }

            competentAuthorityService.exclusiveLockCompetentAuthority(currentUser.getCompetentAuthority());

            requestCreateByCAValidatorOpt.get().validateAction(currentUser.getCompetentAuthority(), payload);
        }
    }
}
