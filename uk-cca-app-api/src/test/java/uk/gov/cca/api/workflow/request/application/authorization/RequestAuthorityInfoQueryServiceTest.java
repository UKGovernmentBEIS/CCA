package uk.gov.cca.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.cca.api.workflow.request.application.authorization.RequestAuthorityInfoQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.service.RequestService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RequestAuthorityInfoQueryServiceTest {
    @InjectMocks
    private RequestAuthorityInfoQueryService service;

    @Mock
    private RequestService requestService;

    @Test
    void getRequestInfo() {
        Request request = Request.builder()
                .accountId(1L)
                .competentAuthority(ENGLAND)
                .status(RequestStatus.IN_PROGRESS)
                .type(mock(RequestType.class))
                .build();

        when(requestService.findRequestById("1")).thenReturn(request);

        RequestAuthorityInfoDTO requestInfoDTO = service.getRequestInfo("1");

        RequestAuthorityInfoDTO expectedRequestInfoDTO = RequestAuthorityInfoDTO.builder()
                .authorityInfo(ResourceAuthorityInfo.builder().accountId(1L).competentAuthority(ENGLAND).build())
                .build();
        assertEquals(expectedRequestInfoDTO, requestInfoDTO);
    }

    @Test
    void getRequestInfo_does_not_exist() {
        when(requestService.findRequestById("1")).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.getRequestInfo("1");
        });
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
