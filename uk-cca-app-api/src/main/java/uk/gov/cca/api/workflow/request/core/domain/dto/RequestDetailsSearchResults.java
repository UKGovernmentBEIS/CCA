package uk.gov.cca.api.workflow.request.core.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestDetailsSearchResults {

    private List<RequestDetailsDTO> requestDetails;
    private Long total;
    
}
