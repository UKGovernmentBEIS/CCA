package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCreateActionHandlerTest {

	@InjectMocks
    private UnderlyingAgreementVariationCreateActionHandler handler;

	@Mock
	private UnderlyingAgreementQueryService underlyingAgreementQueryService;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;

	@Test
	void process() {
		final Long accountId = 1L;
		final String userId = "userId";
		final String type = "UNDERLYING_AGREEMENT_VARIATION";
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		final AppUser appUser = AppUser.builder().userId(userId).roleType(SECTOR_USER).build();
		final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder().build();
		final int version = 1;
                
        RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                .accountId(accountId)
                .requestPayload(UnderlyingAgreementVariationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
						.underlyingAgreementVersion(version)
                        .originalUnderlyingAgreementContainer(originalContainer)
                        .sectorUserAssignee(userId)
                        .build())
                .build();

		when(underlyingAgreementQueryService.getConsolidationNumber(accountId))
				.thenReturn(version);
		when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
				.thenReturn(originalContainer);
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());

		// Invoke
        String result = handler.process(accountId, type, payload, appUser);

		// Verify
        assertThat(result).isEqualTo("1");
		verify(underlyingAgreementQueryService, times(1))
				.getConsolidationNumber(accountId);
		verify(underlyingAgreementQueryService, times(1))
				.getUnderlyingAgreementContainerByAccountId(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void getRequestType() {
		assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);
	}
}
