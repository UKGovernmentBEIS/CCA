package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import java.util.List;

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

@Service
public class UnderlyingAgreementVariationRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

protected TargetUnitAccountRepository targetUnitAccountRepository;
	
	public UnderlyingAgreementVariationRequestIdGenerator(RequestSequenceRepository repository,
			RequestTypeRepository requestTypeRepository,
			TargetUnitAccountRepository targetUnitAccountRepository) {
		super(repository, requestTypeRepository);
		this.targetUnitAccountRepository = targetUnitAccountRepository;
	}

	@Override
    public String generateRequestId(Long sequenceNo, RequestParams params) {
		String businessId = targetUnitAccountRepository.findTargetUnitAccountById(params.getAccountId())
				.map(TargetUnitAccount::getBusinessId)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        return String.format("%s-%s-%d", businessId, getPrefix(), sequenceNo );
    }
	
	@Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);
    }

    @Override
    public String getPrefix() {
        return "VAR";
    }
}
