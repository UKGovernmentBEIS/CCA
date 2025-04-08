package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAndAccountValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class CcaProcessRequestCreateAspect {

    private final List<RequestCreateBySectorAndAccountValidator> requestCreateBySectorAndAccountValidators;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final RequestTypeRepository requestTypeRepository;

    @Before("execution(* uk.gov.cca.api.workflow.request.flow.common.actionhandler.CcaRequestCreateActionHandler.process*(..))")
    public void process(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        final Long sectorAssociationId = (Long) args[0];
        final Long accountId = (Long) args[1];
        final String requestType = (String)args[2];

        if (sectorAssociationId != null) {
            Optional<RequestCreateBySectorAndAccountValidator> requestCreateBySectorAssociationValidatorOpt = requestCreateBySectorAndAccountValidators
                    .stream().filter(requestCreateValidator -> requestCreateValidator.getRequestType().equals(requestType)).findFirst();

            final Set<String> availableRequestTypes = requestTypeRepository.findAllByCanCreateManually(true).stream()
                    .map(RequestType::getCode).collect(Collectors.toSet());

            if (!availableRequestTypes.contains(requestType)) {
                throw new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND,
                        String.format("%s is not supported", requestType));
            }

            sectorAssociationQueryService.exclusiveLockSectorAssociation(sectorAssociationId);

            final RequestCreateValidationResult validationResult = requestCreateBySectorAssociationValidatorOpt
                    .map(requestCreateBySectorAndAccountValidator -> requestCreateBySectorAndAccountValidator.validateAction(sectorAssociationId, accountId))
                    .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build());

            if (!validationResult.isValid() || !validationResult.isAvailable()) {
                throw new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND, validationResult);
            }
        } else {
            throw new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND,
                    String.format("%s is not supported", requestType));
        }
    }
}
