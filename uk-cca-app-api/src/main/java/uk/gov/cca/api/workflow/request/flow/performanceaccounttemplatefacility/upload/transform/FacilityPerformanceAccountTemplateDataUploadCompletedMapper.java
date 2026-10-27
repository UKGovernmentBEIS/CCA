package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestActionPayloadType.class})
public interface FacilityPerformanceAccountTemplateDataUploadCompletedMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_COMPLETED_PAYLOAD)")
    @Mapping(target = "details", source = "performanceAccountTemplateDataUpload")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "uploadAttachments", ignore = true)
    FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload toFacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload(
            FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload);

    @AfterMapping
    default void setAttachments(
            @MappingTarget FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload requestActionPayload,
            FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload) {
        requestActionPayload.setUploadAttachments(taskPayload.getUploadAttachments());
    }
}