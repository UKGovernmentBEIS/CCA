package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationCreateValidatorTest {

	@InjectMocks
    private UnderlyingAgreementVariationCreateValidator validator;
    
    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;
    
    @Test
    void validateAction_whenValid_thenOk() {
    	final Long accountId = 1L;

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<AccountStatus> applicableAccountStatuses = Set.of(TargetUnitAccountStatus.LIVE);
        Set<String> mutuallyExclusiveRequests = Set.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);

        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests))
                .thenReturn(result);

        // Invoke
        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        // Verify
        assertThat(actual.isValid()).isTrue();
        assertThat(actual.getReportedRequestTypes()).isEmpty();
        assertThat(actual.getReportedAccountStatus()).isNull();

        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);
    }
    
    @Test
    void getApplicableAccountStatuses() {
        Set<AccountStatus> accountStatuses = Set.of(TargetUnitAccountStatus.LIVE);

        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(accountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<String> mutuallyExclusiveRequests = Set.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);

        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(mutuallyExclusiveRequests);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);
    }
}
