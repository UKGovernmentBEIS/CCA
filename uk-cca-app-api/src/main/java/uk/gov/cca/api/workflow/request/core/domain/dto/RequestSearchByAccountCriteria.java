package uk.gov.cca.api.workflow.request.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestSearchByAccountCriteria {
    
    @NotNull
    private Long accountId;
    
    @Builder.Default
    private Set<RequestType> requestTypes = new HashSet<>();

    @Builder.Default
    private Set<RequestStatus> requestStatuses = new HashSet<>();

    @NotNull
    private RequestHistoryCategory category;
    
    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
    
    
}
