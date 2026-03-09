package uk.gov.cca.api.workflow.request.flow.noncompliance.details.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmittedRequestActionPayload;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitMapperTest {

    private NonComplianceDetailsSubmitMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(NonComplianceDetailsSubmitMapper.class);
    }

    @Test
    void toNonComplianceDetailsSubmittedRequestActionPayload() {
        final NonComplianceDetails nonComplianceDetails = NonComplianceDetails.builder()
                .nonCompliantDate(LocalDate.now().minusDays(1))
                .relevantWorkflows(Set.of("ADS_1-F00007-AUDT-3"))
                .isEnforcementResponseNoticeRequired(false)
                .build();
        final NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .nonComplianceDetails(nonComplianceDetails)
                .build();

        NonComplianceDetailsSubmittedRequestActionPayload expected = NonComplianceDetailsSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_DETAILS_SUBMITTED_PAYLOAD)
                .nonComplianceDetails(nonComplianceDetails)
                .build();

        // invoke
        NonComplianceDetailsSubmittedRequestActionPayload actual = mapper.toNonComplianceDetailsSubmittedRequestActionPayload(requestTaskPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
