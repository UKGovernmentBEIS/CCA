package uk.gov.cca.api.workflow.request.core.validation;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.config.FeatureFlagProperties;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.validation.EnabledWorkflowValidator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnabledWorkflowValidatorTest {

    @Test
    void isWorkflowNotAllowed() {
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        FeatureFlagProperties featureFlagProperties = new FeatureFlagProperties();
        featureFlagProperties.setDisabledWorkflows(Set.of(requestType));

        EnabledWorkflowValidator enabledWorkflowValidator = new EnabledWorkflowValidator(featureFlagProperties);

        boolean isAllowed = enabledWorkflowValidator.isWorkflowEnabled(requestType);

        assertFalse(isAllowed);
    }

    @Test
    void isWorkflowAllowed_when_all_workflows_enabled() {
        RequestType requestType = RequestType.DUMMY_REQUEST_TYPE;
        FeatureFlagProperties featureFlagProperties = new FeatureFlagProperties();

        EnabledWorkflowValidator enabledWorkflowValidator = new EnabledWorkflowValidator(featureFlagProperties);

        boolean isAllowed = enabledWorkflowValidator.isWorkflowEnabled(requestType);

        assertTrue(isAllowed);

        isAllowed = enabledWorkflowValidator.isWorkflowEnabled(requestType);

        assertTrue(isAllowed);
    }

}
