package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
@RequiredArgsConstructor
public class TargetUnitMoaCreateRequestService {

	private final RequestService requestService;
	private final StartProcessRequestService startProcessRequestService;
	private final AccountReferenceDetailsService accountReferenceDetailsService;
	
	@Transactional
	public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
		final Request parentRequest = requestService.findRequestById(parentRequestId);
		final TargetUnitAccountDetailsDTO targetUnitAccountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
		final Long sectorAssociationId = targetUnitAccountDetails.getSectorAssociationId();

		final RequestParams requestParams = RequestParams.builder()
				.type(CcaRequestType.TARGET_UNIT_MOA)
				.requestResources(Map.of(
						ResourceType.CA, parentRequest.getCompetentAuthority().name(),
						ResourceType.ACCOUNT, accountId.toString(),
						CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
				))
				.requestPayload(TargetUnitMoaRequestPayload.builder()
						.payloadType(CcaRequestPayloadType.TARGET_UNIT_MOA_REQUEST_PAYLOAD)
						.build())
				.requestMetadata(TargetUnitMoaRequestMetadata.builder()
						.type(CcaRequestMetadataType.TARGET_UNIT_MOA)
						.parentRequestId(parentRequestId)
						.businessId(targetUnitAccountDetails.getBusinessId())
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.SUBSISTENCE_FEES_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountId
				))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
