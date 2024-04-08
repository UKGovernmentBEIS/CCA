package uk.gov.cca.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cca.api.notification.template.config.DocumentGeneratorProperties;
import uk.gov.cca.api.notification.template.config.RestEndPointEnum;
import uk.gov.cca.api.notification.template.domain.dto.DocumentParameters;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.restclient.AppRestApi;

@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentGeneratorRemoteClientService implements DocumentGeneratorClientService {

    private final RestTemplate restTemplate;
    private final DocumentGeneratorProperties properties;

    @Override
    public byte[] generateDocument(byte[] source, String outputFilename) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(source) {
            @Override
            public String getFilename() {
                return "source";
            }
        });
        body.add("parameters", DocumentParameters.builder().outputFilename(outputFilename).build());

        final AppRestApi appRestApi = AppRestApi.builder()
                .baseUrl(properties.getUrl())
                .restEndPoint(RestEndPointEnum.GENERATE)
                .headers(headers)
                .restTemplate(restTemplate)
                .body(body)
                .build();

        try {
            final ResponseEntity<byte[]> res = appRestApi.performApiCall();
            return res.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e.getMessage());
        }
    }

}
