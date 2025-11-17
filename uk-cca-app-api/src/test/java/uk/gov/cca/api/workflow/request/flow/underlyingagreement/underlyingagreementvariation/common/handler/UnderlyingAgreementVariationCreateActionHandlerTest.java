package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.configuration.WorkflowSchemeVersionConfig;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCreateActionHandlerTest {

	@InjectMocks
    private UnderlyingAgreementVariationCreateActionHandler handler;

	@Mock
	private WorkflowSchemeVersionConfig workflowSchemeVersionConfig;

	@Mock
	private UnderlyingAgreementQueryService underlyingAgreementQueryService;
	
	@Mock
	private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;

	@Test
	void process() {
		final Long accountId = 1L;
		final Long sectorId = 2L;
		final String userId = "userId";
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		final AppUser appUser = AppUser.builder().userId(userId).roleType(SECTOR_USER).build();
		final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder().build();
		final int version = 1;
		final Map<SchemeVersion, Integer> consolidationNumberMap = Map.of(SchemeVersion.CCA_3, version);
		final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
                
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                .requestResources(Map.of(
				ResourceType.ACCOUNT, accountId.toString(), 
				CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
				))
                .requestPayload(UnderlyingAgreementVariationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
						.workflowSchemeVersion(workflowSchemeVersion)
						.underlyingAgreementVersionMap(consolidationNumberMap)
                        .originalUnderlyingAgreementContainer(originalContainer)
                        .sectorUserAssignee(userId)
                        .build())
				.requestMetadata(UnderlyingAgreementVariationRequestMetadata.builder()
						.type(CcaRequestMetadataType.UNDERLYING_AGREEMENT_VARIATION)
						.workflowSchemeVersion(workflowSchemeVersion)
						.build())
                .build();

		when(workflowSchemeVersionConfig.getUnaVariation()).thenReturn(workflowSchemeVersion);
		when(underlyingAgreementQueryService.getConsolidationNumberMap(accountId))
				.thenReturn(consolidationNumberMap);
		when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
				.thenReturn(originalContainer);
		when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId)).thenReturn(Map.of(
				ResourceType.ACCOUNT, accountId.toString(), 
				CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()
				));
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());

		// Invoke
        String result = handler.process(accountId, payload, appUser);

		// Verify
        assertThat(result).isEqualTo("1");
		verify(workflowSchemeVersionConfig, times(1)).getUnaVariation();
		verify(underlyingAgreementQueryService, times(1)).getConsolidationNumberMap(accountId);
		verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementContainerByAccountId(accountId);
		verify(requestCreateAccountAndSectorResourcesService, times(1)).createRequestResources(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void getRequestType() {
		assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);
	}
}
