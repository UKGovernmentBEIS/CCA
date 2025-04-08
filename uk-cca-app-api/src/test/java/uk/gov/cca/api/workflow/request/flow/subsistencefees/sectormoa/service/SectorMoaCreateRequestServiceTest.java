package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class SectorMoaCreateRequestServiceTest {

    @InjectMocks
    private SectorMoaCreateRequestService service;

    @Mock
    private RequestService requestService;

    @Mock
    private SectorReferenceDetailsService sectorReferenceDetailsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        Long sectorId = 1L;
        String parentRequestId = "parentRequestId";
        String parentRequestBusinessKey = "parentRequestBusinessKey";
        String acronym = "ADS";

        SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().acronym(acronym).build();

        SectorMoaRequestMetadata metadata = SectorMoaRequestMetadata.builder()
                .type(CcaRequestMetadataType.SECTOR_MOA)
                .build();

        Request request = Request.builder()
                .metadata(metadata)
                .build();
        request.getRequestResources().add(RequestResource.builder().resourceId("ENGLAND").resourceType("CA").build());

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(sectorReferenceDetailsService.getSectorAssociationInfo(sectorId)).thenReturn(sectorAssociationInfo);

        service.createRequest(sectorId, parentRequestId, parentRequestBusinessKey);

        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(startProcessRequestService, times(1)).startProcess(RequestParams.builder()
                .type(CcaRequestType.SECTOR_MOA)
                .requestResources(Map.of(
                        ResourceType.CA, "ENGLAND",
                        CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
                ))
                .requestPayload(SectorMoaRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.SECTOR_MOA_REQUEST_PAYLOAD)
                        .sectorAssociationId(sectorId)
                        .build())
                .requestMetadata(SectorMoaRequestMetadata.builder()
                        .type(CcaRequestMetadataType.SECTOR_MOA)
                        .parentRequestId(parentRequestId)
                        .sectorAcronym(acronym)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.SUBSISTENCE_FEES_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.SECTOR_ID, sectorId
                ))
                .build());
    }
}
