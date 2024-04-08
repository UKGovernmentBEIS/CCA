package uk.gov.cca.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.repository.RequestDetailsRepository;
import uk.gov.cca.api.workflow.request.core.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestQueryServiceTest {

    @InjectMocks
    private RequestQueryService service;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestDetailsRepository requestDetailsRepository;
    
    @Test
    void findInProgressRequestsByAccount() {
        Long accountId = 1L;
        Request request = Request.builder().id("1").status(RequestStatus.IN_PROGRESS).build();

        when(requestRepository.findByAccountIdAndStatus(accountId, RequestStatus.IN_PROGRESS)).thenReturn(List.of(request));
        
        List<Request> result = service.findInProgressRequestsByAccount(accountId);
        
        assertThat(result).containsExactlyInAnyOrder(request);
        verify(requestRepository, times(1)).findByAccountIdAndStatus(accountId, RequestStatus.IN_PROGRESS);
    }

    @Test
    void existsRequestById() {
        String requestId = "requestId";

        when(requestRepository.existsById(requestId)).thenReturn(true);

        boolean result = service.existsRequestById(requestId);

        assertThat(result).isTrue();
        verify(requestRepository, times(1)).existsById(requestId);
    }

    @Test
    void existsRequestByAccountAndType() {
        final long accountId = 1L;
        final RequestType requestType = mock(RequestType.class);

        when(requestRepository.existsByAccountIdAndType(accountId, requestType)).thenReturn(true);

        boolean result = service.existsRequestByAccountAndType(accountId, requestType);

        assertThat(result).isTrue();
        verify(requestRepository, times(1)).existsByAccountIdAndType(accountId, requestType);
    }
    
    @Test
    void existByRequestTypeAndRequestStatusAndCompetentAuthority() {
        final RequestType type = mock(RequestType.class);
    	RequestStatus status = RequestStatus.IN_PROGRESS;
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        when(requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority)).thenReturn(true);

        boolean result = service.existByRequestTypeAndRequestStatusAndCompetentAuthority(type, status, competentAuthority);

        assertThat(result).isTrue();
        verify(requestRepository, times(1)).existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
    }

    @Test
    void findRequestDetailsBySearchCriteria() {
        Long accountId = 1L;
        final String requestId = "1";
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().accountId(accountId)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build()).build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, mock(RequestType.class), RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        RequestDetailsDTO workflowResult2 = new RequestDetailsDTO(requestId, mock(RequestType.class), RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults expectedResults = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1, workflowResult2))
                .total(10L)
                .build();

        when(requestDetailsRepository.findRequestDetailsBySearchCriteria(criteria)).thenReturn(expectedResults);

        RequestDetailsSearchResults actualResults = service.findRequestDetailsBySearchCriteria(criteria);

        assertThat(actualResults).isEqualTo(expectedResults);
        verify(requestDetailsRepository, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void findRequestDetailsById() {
        final String requestId = "1";
        RequestDetailsDTO expected = new RequestDetailsDTO(requestId, mock(RequestType.class), RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        when(requestDetailsRepository.findRequestDetailsById(requestId)).thenReturn(Optional.of(expected));

        RequestDetailsDTO actual = service.findRequestDetailsById(requestId);

        assertThat(actual).isEqualTo(expected);
        verify(requestDetailsRepository, times(1)).findRequestDetailsById(requestId);
    }
    
    @Test
    void findRequestDetailsById_not_found() {
        final String requestId = "1";

        when(requestDetailsRepository.findRequestDetailsById(requestId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.findRequestDetailsById(requestId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(requestDetailsRepository, times(1)).findRequestDetailsById(requestId);
    }
}
