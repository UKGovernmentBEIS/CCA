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
class UserRoleTypeSectorRequestReleaseServiceTest {

    @InjectMocks
    private UserRoleTypeSectorUserRequestReleaseService userRoleTypeSectorUserRequestReleaseService;

    @Test
    void getRoleType() {
        assertThat(userRoleTypeSectorUserRequestReleaseService.getRoleType()).isEqualTo(SECTOR_USER);
    }

    @Test
    void release() {
        CcaRequestPayload payload = CcaRequestPayload.builder()
                .sectorUserAssignee("assignee")
                .build();
        Request request = Request.builder()
                .payload(payload)
                .build();

        userRoleTypeSectorUserRequestReleaseService.release(request, "assignee");

        assertThat(payload.getSectorUserAssignee()).isNull();
    }

    @Test
    void release_new_user_is_null() {
        CcaRequestPayload payload = CcaRequestPayload.builder()
                .sectorUserAssignee("assignee")
                .build();
        Request request = Request.builder()
                .payload(payload)
                .build();

        userRoleTypeSectorUserRequestReleaseService.release(request, null);

        assertThat(payload.getSectorUserAssignee()).isNull();
    }

    @Test
    void release_new_user_is_empty() {
        CcaRequestPayload payload = CcaRequestPayload.builder()
                .sectorUserAssignee("assignee")
                .build();
        Request request = Request.builder()
                .payload(payload)
                .build();

        userRoleTypeSectorUserRequestReleaseService.release(request, "");

        assertThat(payload.getSectorUserAssignee()).isNull();
    }
}
