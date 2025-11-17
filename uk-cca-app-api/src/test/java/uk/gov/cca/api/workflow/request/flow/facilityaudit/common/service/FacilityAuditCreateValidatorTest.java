package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityAuditCreateValidatorTest {

    @InjectMocks
    private FacilityAuditCreateValidator validator;

    @Mock
    private CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Test
    void validateAction_whenValid_thenOk() {
        final Long facilityId = 1L;

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<String> mutuallyExclusiveRequests = Set.of(CcaRequestType.FACILITY_AUDIT);

        when(ccaRequestCreateValidatorService.validate(facilityId, CcaResourceType.FACILITY, mutuallyExclusiveRequests))
                .thenReturn(result);

        // Invoke
        final RequestCreateValidationResult actual = validator.validateAction(facilityId);

        // Verify
        assertThat(actual.isValid()).isTrue();
        assertThat(actual.getReportedRequestTypes()).isEmpty();
        assertThat(actual.getReportedAccountStatus()).isNull();

        verify(ccaRequestCreateValidatorService, times(1))
                .validate(facilityId, CcaResourceType.FACILITY, mutuallyExclusiveRequests);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<String> mutuallyExclusiveRequests = Set.of(CcaRequestType.FACILITY_AUDIT);

        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(mutuallyExclusiveRequests);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.FACILITY_AUDIT);
    }
}
