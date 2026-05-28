package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealTribunalDecision;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealOutcomeMapperTest {

    private NonComplianceAppealOutcomeMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(NonComplianceAppealOutcomeMapper.class);
    }

    @Test
    void toNonComplianceAppealOutcomeSubmittedRequestActionPayload() {
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final String regulatorAssignee = appUser.getUserId();

        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();

        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .regulatorAssignee(regulatorAssignee)
                .build();

        final NonComplianceAppealOutcomeSubmittedRequestActionPayload expected = NonComplianceAppealOutcomeSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMITTED_PAYLOAD)
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .build();

        // invoke
        NonComplianceAppealOutcomeSubmittedRequestActionPayload actual =
                mapper.toNonComplianceAppealOutcomeSubmittedRequestActionPayload(requestPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
