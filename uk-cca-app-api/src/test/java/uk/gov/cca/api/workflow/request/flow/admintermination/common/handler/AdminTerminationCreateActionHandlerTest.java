package uk.gov.cca.api.workflow.request.flow.admintermination.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class AdminTerminationCreateActionHandlerTest {

	@InjectMocks
    private AdminTerminationCreateActionHandler handler;

	@Mock
	private UnderlyingAgreementQueryService underlyingAgreementQueryService;
	
	@Mock
    private StartProcessRequestService startProcessRequestService;

	@Test
	void process() {
		final Long accountId = 1L;
		final String userId = "userId";
		final String type = "ADMIN_TERMINATION";
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
		final AppUser appUser = AppUser.builder().userId(userId).roleType(REGULATOR).build();
		final int version = 1;

		final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.ADMIN_TERMINATION)
                .accountId(accountId)
                .requestPayload(AdminTerminationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.ADMIN_TERMINATION_REQUEST_PAYLOAD)
						.underlyingAgreementVersion(version)
                        .regulatorAssignee(userId)
                        .build())
                .build();

		when(underlyingAgreementQueryService.getConsolidationNumber(accountId)).thenReturn(version);
        when(startProcessRequestService.startProcess(requestParams))
        	.thenReturn(Request.builder().id("1").build());

		// Invoke
        String result = handler.process(accountId, type, payload, appUser);

		// Verify
        assertThat(result).isEqualTo("1");
		verify(underlyingAgreementQueryService, times(1)).getConsolidationNumber(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
	}
	
	@Test
	void getRequestType() {
		assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.ADMIN_TERMINATION);
	}
}
