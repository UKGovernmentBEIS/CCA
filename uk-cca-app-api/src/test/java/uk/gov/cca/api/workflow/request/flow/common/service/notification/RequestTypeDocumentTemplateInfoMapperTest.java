package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTypeDocumentTemplateInfoMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RequestTypeDocumentTemplateInfoMapperTest {

    @Test
    void test() {
        assertThat(RequestTypeDocumentTemplateInfoMapper.getTemplateInfo(mock(RequestType.class))).isEqualTo("N/A");
    }
}
