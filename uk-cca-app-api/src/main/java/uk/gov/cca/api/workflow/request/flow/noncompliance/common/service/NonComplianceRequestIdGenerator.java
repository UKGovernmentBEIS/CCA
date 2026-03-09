package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class NonComplianceRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    protected TargetUnitAccountRepository targetUnitAccountRepository;

    public NonComplianceRequestIdGenerator(RequestSequenceRepository repository, RequestTypeRepository requestTypeRepository,
                                           TargetUnitAccountRepository targetUnitAccountRepository) {
        super(repository, requestTypeRepository);
        this.targetUnitAccountRepository = targetUnitAccountRepository;
    }

    @Override
    public String generateRequestId(Long sequenceNo, RequestParams params) {
        String businessId = targetUnitAccountRepository.findTargetUnitAccountById(params.getAccountId())
                .map(TargetUnitAccount::getBusinessId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        return String.format("%s-%s-%d", businessId, getPrefix(), sequenceNo);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.NON_COMPLIANCE);
    }

    @Override
    public String getPrefix() {
        return "NCOM";
    }
}
