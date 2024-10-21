package uk.gov.cca.api.workflow.request.core.assignment.requestassign.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
public class UserRoleTypeSectorRequestAssignmentServiceTest {

    @InjectMocks
    private UserRoleTypeSectorUserRequestAssignmentService userRoleTypeSectorUserRequestAssignmentService;

    @Test
    void getRoleType() {
        assertThat(userRoleTypeSectorUserRequestAssignmentService.getRoleType()).isEqualTo(SECTOR_USER);
    }

    @Test
    void assign() {
        CcaRequestPayload payload = CcaRequestPayload.builder().build();
        Request request = Request.builder()
                .payload(payload)
                .build();

        userRoleTypeSectorUserRequestAssignmentService.assign(request, "userId");

        assertThat(payload.getSectorUserAssignee()).isEqualTo("userId");
    }
}
