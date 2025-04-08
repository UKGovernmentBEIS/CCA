package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaCreateRequestServiceTest {

	@InjectMocks
	private TargetUnitMoaCreateRequestService service;

	@Mock
	private RequestService requestService;
	
	@Mock
	private AccountReferenceDetailsService accountReferenceDetailsService;
	
	@Mock
	private StartProcessRequestService startProcessRequestService;
	
	@Test
	void createRequest() {
		Long accountId = 1L;
		String parentRequestId = "parentRequestId";
		String parentRequestBusinessKey = "parentRequestBusinessKey";
		String businessId = "businessId";
		
		TargetUnitAccountDetailsDTO targetUnitAccountDetails = TargetUnitAccountDetailsDTO.builder()
				.businessId(businessId)
				.sectorAssociationId(1L)
				.build();
		
		TargetUnitMoaRequestMetadata metadata = TargetUnitMoaRequestMetadata.builder()
				.type(CcaRequestMetadataType.TARGET_UNIT_MOA)
				.build();
		
		Request request = Request.builder()
				.metadata(metadata)
				.build();
		request.getRequestResources().add(RequestResource.builder().resourceId("ENGLAND").resourceType("CA").build());
		request.getRequestResources().add(RequestResource.builder().resourceId("1").resourceType("SECTOR_ASSOCIATION").build());
		
		when(requestService.findRequestById(parentRequestId)).thenReturn(request);
		when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(targetUnitAccountDetails);
		
		service.createRequest(accountId, parentRequestId, parentRequestBusinessKey);
		
		verify(requestService, times(1)).findRequestById(parentRequestId);
		verify(startProcessRequestService, times(1)).startProcess(RequestParams.builder()
				.type(CcaRequestType.TARGET_UNIT_MOA)
				.requestResources(Map.of(
						ResourceType.CA, "ENGLAND",
						ResourceType.ACCOUNT, accountId.toString(),
						CcaResourceType.SECTOR_ASSOCIATION, "1"
				))
				.requestPayload(TargetUnitMoaRequestPayload.builder()
						.payloadType(CcaRequestPayloadType.TARGET_UNIT_MOA_REQUEST_PAYLOAD)
						.build())
				.requestMetadata(TargetUnitMoaRequestMetadata.builder()
						.type(CcaRequestMetadataType.TARGET_UNIT_MOA)
						.parentRequestId(parentRequestId)
						.businessId(businessId)
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.SUBSISTENCE_FEES_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountId
						))
				.build());
	}
}
