package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadProcessingActionHandlerTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadProcessingActionHandler handler;

    @Test
    void getRequestType() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING);
    }
}
