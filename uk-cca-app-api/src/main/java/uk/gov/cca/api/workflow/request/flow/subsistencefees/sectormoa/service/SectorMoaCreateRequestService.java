package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SectorMoaCreateRequestService {

	private final RequestService requestService;
	private final StartProcessRequestService startProcessRequestService;
	private final SectorReferenceDetailsService sectorReferenceDetailsService;
	
	@Transactional
	public void createRequest(Long sectorId, String parentRequestId, String parentRequestBusinessKey) {
		final Request parentRequest = requestService.findRequestById(parentRequestId);
		final SectorAssociationInfo sectorAssociationInfo = sectorReferenceDetailsService.getSectorAssociationInfo(sectorId);

		final RequestParams requestParams = RequestParams.builder()
				.type(CcaRequestType.SECTOR_MOA)
				.requestResources(Map.of(
						ResourceType.CA, parentRequest.getCompetentAuthority().name(),
						CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
				))
				.requestPayload(SectorMoaRequestPayload.builder()
						.payloadType(CcaRequestPayloadType.SECTOR_MOA_REQUEST_PAYLOAD)
						.sectorAssociationId(sectorId)
						.build())
				.requestMetadata(SectorMoaRequestMetadata.builder()
						.type(CcaRequestMetadataType.SECTOR_MOA)
						.parentRequestId(parentRequestId)
						.sectorAcronym(sectorAssociationInfo.getAcronym())
						.build())
				.processVars(Map.of(
						CcaBpmnProcessConstants.SUBSISTENCE_FEES_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
						CcaBpmnProcessConstants.SECTOR_ID, sectorId
				))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
