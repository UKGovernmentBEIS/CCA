package uk.gov.cca.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.cca.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityResourceAdapterTest {

    @InjectMocks
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Test
    void getUserScopedRequestTaskTypes() {
        final String userId = "userId";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;

        when(regulatorAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
            .thenReturn(Map.of(
                competentAuthority,
                Set.of("DUMMY_REQUEST_TYPE_APPLICATION_REVIEW")));

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> userScopedRequestTaskTypes =
            regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypes(userId);

        assertThat(userScopedRequestTaskTypes).containsExactlyEntriesOf(
            Map.of(
                competentAuthority, Set.of(RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW)
            )
        );
    }
}