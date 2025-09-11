package uk.gov.cca.api.workflow.request.flow.admintermination.submit.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitMapperTest {

    private AdminTerminationSubmitMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(AdminTerminationSubmitMapper.class);
    }

    @Test
    void toApplicationSubmitRequestTaskPayload() {

        final UUID fileUuid = UUID.randomUUID();
        final String explanation = "bla bla bla bla";
        final Map<UUID, String> attachments = Map.of(fileUuid, "attachedFile");
        Map<String, String> sectionsCompleted = Map.of("sectionA", "COMPLETED");
        final AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                        .reason(AdminTerminationReason.FAILURE_TO_COMPLY)
                        .explanation(explanation)
                        .build())
                .sectionsCompleted(sectionsCompleted)
                .adminTerminationSubmitAttachments(attachments)
                .build();

        AdminTerminationSubmitRequestTaskPayload applicationSubmitRequestTaskPayload =
                mapper.toApplicationSubmitRequestTaskPayload(requestPayload);

        assertThat(applicationSubmitRequestTaskPayload.getAdminTerminationReasonDetails())
                .isEqualTo(requestPayload.getAdminTerminationReasonDetails());
        assertThat(applicationSubmitRequestTaskPayload.getAdminTerminationAttachments())
                .isEqualTo(requestPayload.getAdminTerminationSubmitAttachments());
        assertThat(applicationSubmitRequestTaskPayload.getSectionsCompleted())
                .isEqualTo(requestPayload.getSectionsCompleted());
    }

}
