package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadCompleteActionHandlerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadCompleteActionHandler cut;
	
	@Test
	void getType() {
		assertThat(cut.getTypes()).containsExactlyElementsOf(
				List.of(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE));
	}
	
}
