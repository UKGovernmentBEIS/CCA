package uk.gov.cca.api.workflow.request.core.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import uk.gov.cca.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestAction;
import uk.gov.cca.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestActionInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;

import static org.assertj.core.api.Assertions.assertThat;

class RequestActionMapperTest {

    private RequestActionMapper mapper;
    
    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestActionMapper.class);
    }
    
    @Test
    void toRequestActionDTO() {
        Long accountId = 100L;
        Request request = Request.builder()
        		.id("requestId")
        		.accountId(accountId)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.type(RequestType.DUMMY_REQUEST_TYPE).build();
        RequestActionPayload requestActionPayload = Mockito.mock(RequestActionPayload.class);
        RequestAction requestAction = RequestAction.builder()
            .id(1L)
            .type(RequestActionType.REQUEST_TERMINATED)
            .submitter("fn ln")
            .payload(requestActionPayload)
            .request(request)
            .build();
        
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);
        
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(RequestActionType.REQUEST_TERMINATED);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getPayload()).isEqualTo(requestActionPayload);
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestType()).isEqualTo(RequestType.DUMMY_REQUEST_TYPE);
        assertThat(result.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }
    
    @Test
    void toRequestActionInfoDTO() {
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .type(RequestActionType.REQUEST_TERMINATED)
                .submitter("fn ln")
                .build();
        
        RequestActionInfoDTO result = mapper.toRequestActionInfoDTO(requestAction);
        assertThat(result.getType()).isEqualTo(RequestActionType.REQUEST_TERMINATED);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
    }
    
    @Test
    void toRequestActionDTOIgnorePayload() {
        Long accountId = 100L;
        Request request = Request.builder()
        		.id("requestId")
        		.accountId(accountId)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.type(RequestType.DUMMY_REQUEST_TYPE).build();
        RequestAction requestAction = RequestAction.builder()
                .id(1L)
                .payload(PaymentCancelledRequestActionPayload.builder().payloadType(RequestActionPayloadType.PAYMENT_CANCELLED_PAYLOAD).build())
                .type(RequestActionType.PAYMENT_CANCELLED)
                .submitter("fn ln")
                .request(request)
                .build();
        
        RequestActionDTO result = mapper.toRequestActionDTOIgnorePayload(requestAction);
        assertThat(result.getPayload()).isNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        assertThat(result.getRequestAccountId()).isEqualTo(accountId);
        assertThat(result.getRequestId()).isEqualTo("requestId");
        assertThat(result.getRequestType()).isEqualTo(RequestType.DUMMY_REQUEST_TYPE);
        assertThat(result.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
    }
}
