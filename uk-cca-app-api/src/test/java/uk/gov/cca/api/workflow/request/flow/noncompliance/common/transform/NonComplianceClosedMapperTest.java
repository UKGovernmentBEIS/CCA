package uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceClosedRequestActionPayload;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceClosedMapperTest {

    private NonComplianceClosedMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(NonComplianceClosedMapper.class);
    }

    @Test
    void toNonComplianceClosedRequestActionPayload() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason("reason")
                .files(Set.of(fileUuid))
                .build();
        final String regulatorAssignee = "bbb2820b-cbc6-4923-b3f1-8de409ea34c1";
        final Map<UUID, String> attachments = Map.of(fileUuid, "attachment");
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .closeJustification(closeJustification)
                .regulatorAssignee(regulatorAssignee)
                .nonComplianceAttachments(attachments)
                .build();
        final NonComplianceClosedRequestActionPayload expected = NonComplianceClosedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_CLOSED_PAYLOAD)
                .closeJustification(closeJustification)
                .nonComplianceAttachments(attachments)
                .build();

        // invoke
        NonComplianceClosedRequestActionPayload actual = mapper.toNonComplianceClosedRequestActionPayload(requestPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
    }

}