package uk.gov.cca.api.workflow.request.flow.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PeerReviewMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    PeerReviewDecisionSubmittedRequestActionPayload toPeerReviewDecisionSubmittedRequestActionPayload(
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload, RequestActionPayloadType payloadType);
}
