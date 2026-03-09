package uk.gov.cca.api.workflow.request.flow.noncompliance.details.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.validation.NonComplianceDetailsSubmitValidator;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitValidatorTest {

    @InjectMocks
    private NonComplianceDetailsSubmitValidator validator;

    @Mock
    private DataValidator<NonComplianceDetails> dataValidator;

    @Test
    void validate_valid() {
        final NonComplianceDetails nonComplianceDetails = NonComplianceDetails.builder()
                .nonCompliantDate(LocalDate.now().minusDays(1))
                .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                .isEnforcementResponseNoticeRequired(false)
                .build();
        final NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD)
                .nonComplianceDetails(nonComplianceDetails)
                .build();

        when(dataValidator.validate(nonComplianceDetails)).thenReturn(Optional.empty());

        // invoke
        validator.validate(requestTaskPayload);

        // verify
        verify(dataValidator, times(1)).validate(nonComplianceDetails);
    }

    @Test
    void validate_not_valid() {
        final NonComplianceDetails nonComplianceDetails = NonComplianceDetails.builder()
                .nonCompliantDate(LocalDate.now().plusDays(1))
                .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                .isEnforcementResponseNoticeRequired(true)
                .build();
        final NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD)
                .nonComplianceDetails(nonComplianceDetails)
                .build();

        when(dataValidator.validate(nonComplianceDetails)).thenReturn(Optional.of(new NonComplianceViolation(NonComplianceDetails.class.getName(),
                NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_DETAILS_DATA)));

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(requestTaskPayload));

        // verify
        assertThat(CcaErrorCode.INVALID_NON_COMPLIANCE).isEqualTo(businessException.getErrorCode());
        verify(dataValidator, times(1)).validate(nonComplianceDetails);
    }
}
