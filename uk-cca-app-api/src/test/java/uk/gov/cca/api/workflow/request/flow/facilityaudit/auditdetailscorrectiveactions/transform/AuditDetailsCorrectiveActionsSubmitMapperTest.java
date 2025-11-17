package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditTechnique;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditDetailsCorrectiveActionsSubmitMapperTest {

    private AuditDetailsCorrectiveActionsSubmitMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(AuditDetailsCorrectiveActionsSubmitMapper.class);
    }

    @Test
    void toAuditDetailsCorrectiveActionsSubmittedRequestActionPayload() {
        final UUID fileUuid = UUID.randomUUID();
        final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload requestTaskPayload = AuditDetailsCorrectiveActionsSubmitRequestTaskPayload.builder()
                .auditDetailsAndCorrectiveActions(AuditDetailsAndCorrectiveActions.builder()
                        .auditDetails(AuditDetails.builder()
                                .auditTechnique(AuditTechnique.DESK_BASED_INTERVIEW)
                                .auditDate(LocalDate.of(2025, 2, 2))
                                .finalAuditReportDate(LocalDate.of(2025, 2, 2))
                                .comments("bla bla bla bla")
                                .auditDocuments(Set.of(fileUuid))
                                .build())
                        .correctiveActions(CorrectiveActions.builder()
                                .hasActions(true)
                                .actions(Set.of(CorrectiveAction.builder()
                                        .title("title")
                                        .deadline(LocalDate.of(2022, 3, 3))
                                        .details("bla bla bla bla")
                                        .build()))
                                .build())

                        .build())
                .sectionsCompleted(Map.of("section1", "COMPLETED"))
                .facilityAuditAttachments(Map.of(fileUuid, "filename"))
                .build();

        AuditDetailsCorrectiveActionsSubmittedRequestActionPayload requestActionPayload =
                mapper.toAuditDetailsCorrectiveActionsSubmittedRequestActionPayload(requestTaskPayload);

        assertThat(requestActionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED_PAYLOAD);
        assertThat(requestTaskPayload.getAuditDetailsAndCorrectiveActions())
                .isEqualTo(requestActionPayload.getAuditDetailsAndCorrectiveActions());
        assertThat(requestActionPayload.getAttachments()).isEqualTo(requestTaskPayload.getAttachments());
    }
}
