package uk.gov.cca.api.workflow.request.flow.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaPeerReviewMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "peerReviewAttachments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    CcaPeerReviewDecisionSubmittedRequestActionPayload toPeerReviewDecisionSubmittedRequestActionPayload(CcaPeerReviewDecisionRequestTaskPayload requestTaskPayload, String payloadType);


    @AfterMapping
    default void setPeerReviewAttachments(@MappingTarget CcaPeerReviewDecisionSubmittedRequestActionPayload requestActionPayload,
                                          CcaPeerReviewDecisionRequestTaskPayload taskPayload) {
        requestActionPayload.setPeerReviewAttachments(taskPayload.getPeerReviewAttachments());
    }
}
