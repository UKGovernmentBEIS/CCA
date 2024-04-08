package uk.gov.cca.api.workflow.request.flow.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateRequestTypeValidationResult {

    private boolean valid;

    @Builder.Default
    private Set<RequestType> reportedRequestTypes = new HashSet<>();
}
