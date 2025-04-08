package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadCreateActionHandlerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadCreateActionHandler cut;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;

	@Mock
	private SectorReferenceDetailsService sectorReferenceDetailsService;


	@Test
	void getRequestType() {
		assertThat(cut.getRequestType()).isEqualTo(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
	}
    
	@Test
	void process() {
		Long sectorId = 1L;
		RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		AppUser appUser = AppUser.builder().userId("user").build();
		
		SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
				.competentAuthority(CompetentAuthorityEnum.ENGLAND)
				.build();
		when(sectorReferenceDetailsService.getSectorAssociationInfo(sectorId)).thenReturn(sectorAssociationInfo);
		
		CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString(),
                        ResourceType.CA, sectorAssociationInfo.getCompetentAuthority().name()
                ))
                .requestPayload(PerformanceAccountTemplateDataUploadRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_ACCCOUNT_TEMPLATE_DATA_UPLOAD_PAYLOAD)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .sectorUserAssignee(appUser.getUserId())
                        .build())
                .build();
		
		Request request = Request.builder().id("reqId").build();
		when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);
		
		String result = cut.process(sectorId, payload, appUser);
		assertThat(result).isEqualTo("reqId");
		
		
		verify(sectorReferenceDetailsService, times(1)).getSectorAssociationInfo(sectorId);
		verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
}
