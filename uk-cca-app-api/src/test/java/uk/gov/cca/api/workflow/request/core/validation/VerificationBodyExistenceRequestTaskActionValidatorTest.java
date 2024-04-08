package uk.gov.cca.api.workflow.request.core.validation;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.cca.api.workflow.request.core.validation.VerificationBodyExistenceRequestTaskActionValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VerificationBodyExistenceRequestTaskActionValidatorTest {

    private final VerificationBodyExistenceRequestTaskActionValidator validator =
        new VerificationBodyExistenceRequestTaskActionValidator();

    @Test
    void getErrorMessage() {
        assertThat(validator.getErrorMessage()).isEqualTo(RequestTaskActionValidationResult.ErrorMessage.NO_VB_FOUND);
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).isEqualTo(Set.of());
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(Set.of(), validator.getConflictingRequestTaskTypes());
    }

    @Test
    void validate() {
        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().verificationBodyId(1L).build())
            .build();
        assertEquals(RequestTaskActionValidationResult.validResult(), validator.validate(requestTask));
    }

    @Test
    void validate_no_vb() {
        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().build())
            .build();
        assertEquals(RequestTaskActionValidationResult.invalidResult(RequestTaskActionValidationResult.ErrorMessage.NO_VB_FOUND),
            validator.validate(requestTask));
    }
}
