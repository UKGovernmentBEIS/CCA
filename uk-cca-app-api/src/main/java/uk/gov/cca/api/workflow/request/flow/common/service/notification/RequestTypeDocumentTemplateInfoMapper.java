package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class RequestTypeDocumentTemplateInfoMapper {

    private static Map<RequestType, String> map = new HashMap<>();
    
    public String getTemplateInfo(RequestType requestType) {
        return map.getOrDefault(requestType, "N/A");
    }
}
