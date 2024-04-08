package uk.gov.cca.api.workflow.request.core.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.workflow.request.core.transform.RequestSearchCriteriaMapper;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchByAccountCriteria;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestSearchCriteriaMapperTest {

	private RequestSearchCriteriaMapper mapper;
    
    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RequestSearchCriteriaMapper.class);
    }
    
    @Test
    void toRequestSearchCriteria() {
    	RequestSearchByAccountCriteria requestSearchByAccountCriteria = RequestSearchByAccountCriteria.builder()
    			.accountId(1L)
    			.category(RequestHistoryCategory.PERMIT)
    			.paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build())
    			.requestStatuses(Set.of(RequestStatus.COMPLETED))
    			.requestTypes(Set.of(RequestType.DUMMY_REQUEST_TYPE))
    			.build();
    	
    	RequestSearchCriteria result = mapper.toRequestSearchCriteria(requestSearchByAccountCriteria);
    	
    	assertThat(result).isEqualTo(RequestSearchCriteria.builder()
    			.accountId(1L)
    			.category(RequestHistoryCategory.PERMIT)
    			.paging(PagingRequest.builder().pageNumber(1L).pageSize(10L).build())
    			.requestStatuses(Set.of(RequestStatus.COMPLETED))
    			.requestTypes(Set.of(RequestType.DUMMY_REQUEST_TYPE))
    			.build());
    }
}
