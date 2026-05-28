package uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetailsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealDetailsMapperTest {

    private NonComplianceAppealDetailsMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(NonComplianceAppealDetailsMapper.class);
    }

    @Test
    void toNonComplianceAppealDetailsSubmittedRequestActionPayload() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(LocalDate.now().minusDays(1))
                .comments("bla bla bla")
                .build();
        final String regulatorAssignee = "bbb2820b-cbc6-4923-b3f1-8de409ea34c1";
        final Map<UUID, String> attachments = Map.of(fileUuid, "attachment");
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .appealDetails(appealDetails)
                .regulatorAssignee(regulatorAssignee)
                .nonComplianceAttachments(attachments)
                .build();
        final NonComplianceAppealDetailsSubmittedRequestActionPayload expected = NonComplianceAppealDetailsSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED_PAYLOAD)
                .appealDetails(appealDetails)
                .nonComplianceAttachments(attachments)
                .build();

        // invoke
        NonComplianceAppealDetailsSubmittedRequestActionPayload actual = mapper.toNonComplianceAppealDetailsSubmittedRequestActionPayload(requestPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}