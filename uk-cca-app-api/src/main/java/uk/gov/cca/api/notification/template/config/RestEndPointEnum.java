package uk.gov.cca.api.notification.template.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.netz.api.common.restclient.RestEndPoint;

import java.util.List;

@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestEndPoint {

    GENERATE("/generate", HttpMethod.POST, new ParameterizedTypeReference<byte[]>() {}, null);

    private final String endPoint;

    private final HttpMethod method;

    private final ParameterizedTypeReference<?> parameterizedTypeReference;

    private final List<String> parameters;
    
}
