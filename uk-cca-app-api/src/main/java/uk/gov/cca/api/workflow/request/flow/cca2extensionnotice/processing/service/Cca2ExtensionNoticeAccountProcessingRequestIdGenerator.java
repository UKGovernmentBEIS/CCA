package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

import java.util.List;

@Service
public class Cca2ExtensionNoticeAccountProcessingRequestIdGenerator implements RequestIdGenerator {

    private static final String REQUEST_ID_FORMATTER = "%s-%s";

    @Override
    public String generate(RequestParams params) {
        final Cca2ExtensionNoticeAccountProcessingRequestMetadata metadata =
                (Cca2ExtensionNoticeAccountProcessingRequestMetadata) params.getRequestMetadata();

        final String accountAcronym = metadata.getAccountBusinessId();
        final String parentRequestId = metadata.getParentRequestId();

        return String.format(REQUEST_ID_FORMATTER, accountAcronym, parentRequestId);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
