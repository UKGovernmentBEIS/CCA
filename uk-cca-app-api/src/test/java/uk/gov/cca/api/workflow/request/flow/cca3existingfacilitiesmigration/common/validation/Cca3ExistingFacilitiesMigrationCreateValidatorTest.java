package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationCreateValidatorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationCreateValidator validator;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validateAction() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();

        when(requestQueryService
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority))
                .thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(competentAuthority, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
    }

    @Test
    void validateAction_not_valid() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();

        when(requestQueryService
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority))
                .thenReturn(true);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(competentAuthority, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder()
                .valid(false)
                .reportedRequestTypes(Set.of(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN))
                .build());
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN);
    }
}
