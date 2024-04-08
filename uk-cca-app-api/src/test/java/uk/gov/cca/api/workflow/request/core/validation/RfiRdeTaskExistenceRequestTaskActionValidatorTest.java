package uk.gov.cca.api.workflow.request.core.validation;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.validation.RfiRdeTaskExistenceRequestTaskActionValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RfiRdeTaskExistenceRequestTaskActionValidatorTest {

    private final RfiRdeTaskExistenceRequestTaskActionValidator validator = new RfiRdeTaskExistenceRequestTaskActionValidator();

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).isEqualTo(Set.of(
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RDE_SUBMIT)
        );
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(RequestTaskType.getRfiRdeWaitForResponseTypes(), validator.getConflictingRequestTaskTypes());
    }
}
