package uk.gov.cca.api.workflow.request.flow.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.cca.api.workflow.request.core.domain.RequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestParams {

    private RequestType type;
    private Long accountId;
    private LocalDateTime creationDate;
    private CompetentAuthorityEnum competentAuthority;
    private RequestPayload requestPayload;
    private RequestMetadata requestMetadata;
    @Builder.Default
    private Map<String, Object> processVars = new HashMap<>();

    @With
    private String requestId;

}
